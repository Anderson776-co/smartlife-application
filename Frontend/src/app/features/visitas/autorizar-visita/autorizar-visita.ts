import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { Encabezado } from '../../../shared/encabezado/encabezado';
import { Pie } from '../../../shared/pie/pie';
import { MOTIVOS, PROPIEDADES } from '../catalogos';
import {
  AutorizacionCreada,
  SolicitudAutorizacion,
  VisitAuthorizationService,
} from '../visit-authorization.service';
import { VisitaAutorizada } from '../visita-autorizada/visita-autorizada';
import { ResumenVisita } from './resumen-visita';

// Mismas reglas del backend (application.properties y ResolveVisitor)
const TOLERANCIA_INICIO_MS = 10 * 60 * 1000; // 10 minutos
const ANTICIPACION_MAXIMA_MS = 30 * 24 * 60 * 60 * 1000; // 30 días
const DURACION_MAXIMA_MS = 24 * 60 * 60 * 1000; // 24 horas
const TAMANO_MAXIMO_DOCUMENTO = 5 * 1024 * 1024; // 5 MB
const TIPOS_PERMITIDOS = ['application/pdf', 'image/jpeg', 'image/png'];

const PATRON_DOCUMENTO = /^[A-Za-z0-9-]{4,30}$/;
const PATRON_NOMBRE = /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' ]{1,100}$/;
const PATRON_PLACA = /^[A-Za-z0-9-]{3,20}$/;

/** El backend responde con códigos cuando no tiene conectado el catálogo de mensajes. */
const MENSAJES: Record<string, string> = {
  VISIT_DATES_REQUIRED: 'Indica la fecha de inicio y la de fin.',
  VISIT_DATES_INVALID_RANGE: 'La fecha de fin debe ser posterior a la de inicio.',
  VISIT_DATES_IN_THE_PAST: 'La visita no puede estar en el pasado.',
  VISIT_START_TOO_FAR_IN_THE_FUTURE:
    'La visita se puede programar con máximo 30 días de anticipación.',
  VISIT_DURATION_EXCEEDED: 'La autorización no puede durar más de 24 horas.',
  VISIT_OBSERVATION_TOO_LONG: 'La observación no puede superar 500 caracteres.',
  VISIT_DOCUMENT_REQUIRED: 'Este motivo exige adjuntar un documento.',
  VISIT_DOCUMENT_TYPE_NOT_ALLOWED: 'El documento debe ser un PDF, JPG o PNG válido.',
  VISIT_DOCUMENT_TOO_LARGE: 'El documento supera el tamaño máximo de 5 MB.',
  VISIT_AUTHORIZATION_OVERLAPS:
    'Este visitante ya tiene una autorización que se cruza con esas fechas en esta propiedad.',
  VISIT_DOCUMENT_STORAGE_FAILED: 'No se pudo guardar el documento. Inténtalo de nuevo.',
  AUTHORIZATOR_HAS_ACCESS_TO_PROPERTY: 'No tienes acceso a esta propiedad.',
  PROPERTY_VISIT_DOESNT_EXISTS_BY_ID: 'La propiedad seleccionada no existe.',
  VISIT_REASON_DOESNT_EXISTS_BY_ID: 'El motivo seleccionado no existe.',
  USER_AUTHORIZATOR_ISNT_ACTIVE: 'Tu usuario no está activo.',
  USER_AUTHORIZATOR_DOESNT_EXISTS_BY_ID: 'Tu usuario no está habilitado para autorizar visitas.',
  USER_ERROR_TRYING_TO_MAKE_AN_OPERATION:
    'No se pudo completar la operación. Inténtalo de nuevo.',
  VISITOR_DOCUMENT_REQUIRED: 'Indica el documento del visitante.',
  VISITOR_DOCUMENT_INVALID:
    'El documento del visitante solo admite letras, números y guiones (4 a 30 caracteres).',
  VISITOR_FIRST_NAME_INVALID: 'El primer nombre del visitante no es válido.',
  VISITOR_MIDDLE_NAME_INVALID: 'El segundo nombre del visitante no es válido.',
  VISITOR_LAST_NAME_INVALID: 'El primer apellido del visitante no es válido.',
  VISITOR_SECOND_LAST_NAME_INVALID: 'El segundo apellido del visitante no es válido.',
  VISITOR_EMAIL_INVALID: 'El correo del visitante no es válido.',
  VISITOR_PLATE_INVALID: 'La placa solo admite letras, números y guiones (3 a 20 caracteres).',
};

type Campo =
  | 'propiedadId'
  | 'motivoId'
  | 'inicio'
  | 'fin'
  | 'observacion'
  | 'documento'
  | 'primerNombre'
  | 'segundoNombre'
  | 'primerApellido'
  | 'segundoApellido'
  | 'correo'
  | 'placa';

/** Valida las fechas con las mismas reglas que el backend. */
const fechasValidas: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const inicio = new Date(grupo.get('inicio')?.value ?? '').getTime();
  const fin = new Date(grupo.get('fin')?.value ?? '').getTime();
  if (Number.isNaN(inicio) || Number.isNaN(fin)) {
    return null; // el campo vacío ya lo avisa "required"
  }
  const ahora = Date.now();
  if (fin <= inicio) return { rangoInvalido: true };
  if (inicio < ahora - TOLERANCIA_INICIO_MS || fin <= ahora) return { enElPasado: true };
  if (inicio > ahora + ANTICIPACION_MAXIMA_MS) return { muyLejos: true };
  if (fin - inicio > DURACION_MAXIMA_MS) return { duracionExcedida: true };
  return null;
};

@Component({
  selector: 'app-autorizar-visita',
  imports: [ReactiveFormsModule, RouterLink, Encabezado, Pie, VisitaAutorizada],
  templateUrl: './autorizar-visita.html',
  styleUrl: './autorizar-visita.scss',
})
export class AutorizarVisita {
  private readonly fb = inject(FormBuilder);
  private readonly servicio = inject(VisitAuthorizationService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly propiedades = PROPIEDADES;
  protected readonly motivos = MOTIVOS;

  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly resultado = signal<AutorizacionCreada | null>(null);
  protected readonly documento = signal<File | null>(null);
  protected readonly errorDocumento = signal<string | null>(null);

  protected readonly formulario = this.fb.nonNullable.group(
    {
      propiedadId: ['', Validators.required],
      motivoId: ['', Validators.required],
      inicio: ['', Validators.required],
      fin: ['', Validators.required],
      observacion: ['', Validators.maxLength(500)],
      documento: ['', [Validators.required, Validators.pattern(PATRON_DOCUMENTO)]],
      primerNombre: ['', [Validators.required, Validators.pattern(PATRON_NOMBRE)]],
      segundoNombre: ['', Validators.pattern(PATRON_NOMBRE)],
      primerApellido: ['', [Validators.required, Validators.pattern(PATRON_NOMBRE)]],
      segundoApellido: ['', Validators.pattern(PATRON_NOMBRE)],
      correo: ['', [Validators.required, Validators.email]],
      placa: ['', Validators.pattern(PATRON_PLACA)],
    },
    { validators: fechasValidas },
  );

  private readonly motivoSeleccionado = toSignal(this.formulario.controls.motivoId.valueChanges, {
    initialValue: '',
  });

  protected readonly requiereDocumento = computed(
    () => MOTIVOS.find((m) => m.id === this.motivoSeleccionado())?.requiereDocumento ?? false,
  );

  private readonly valores = toSignal(this.formulario.valueChanges, {
    initialValue: this.formulario.getRawValue(),
  });

  protected readonly duracion = computed(() =>
    this.duracionTexto(this.valores().inicio, this.valores().fin),
  );

  protected readonly resumen = computed<ResumenVisita>(() => {
    const v = this.valores();
    const nombre = [v.primerNombre, v.segundoNombre, v.primerApellido, v.segundoApellido]
      .map((parte) => (parte ?? '').trim())
      .filter((parte) => parte.length > 0)
      .join(' ');
    return {
      propiedad: PROPIEDADES.find((p) => p.id === v.propiedadId)?.nombre ?? null,
      motivo: MOTIVOS.find((m) => m.id === v.motivoId)?.nombre ?? null,
      inicio: this.formatoLegible(v.inicio),
      fin: this.formatoLegible(v.fin),
      duracion: this.duracion(),
      visitante: nombre || null,
      documento: (v.documento ?? '').trim() || null,
      correo: (v.correo ?? '').trim().toLowerCase() || null,
    };
  });

  /** Porcentaje de campos obligatorios ya completos y válidos. */
  protected readonly progreso = computed(() => {
    this.valores(); // se recalcula con cada cambio del formulario
    const c = this.formulario.controls;
    const obligatorios = [
      c.propiedadId,
      c.motivoId,
      c.inicio,
      c.fin,
      c.documento,
      c.primerNombre,
      c.primerApellido,
      c.correo,
    ];
    let hechos = obligatorios.filter((campo) => campo.valid).length;
    let total = obligatorios.length;
    if (this.requiereDocumento()) {
      total += 1;
      if (this.documento() && !this.errorDocumento()) hechos += 1;
    }
    return Math.round((hechos / total) * 100);
  });

  constructor() {
    this.formulario.patchValue(this.fechasIniciales());
  }

  protected esInvalido(campo: Campo): boolean {
    const control = this.formulario.controls[campo];
    return control.touched && control.invalid;
  }

  protected elegirArchivo(evento: Event): void {
    const archivo = (evento.target as HTMLInputElement).files?.[0] ?? null;
    this.documento.set(archivo);
    this.errorDocumento.set(this.validarArchivo(archivo));
  }

  protected quitarArchivo(entrada: HTMLInputElement): void {
    entrada.value = '';
    this.documento.set(null);
    this.errorDocumento.set(null);
  }

  protected tamano(archivo: File): string {
    return archivo.size >= 1024 * 1024
      ? `${(archivo.size / (1024 * 1024)).toFixed(1)} MB`
      : `${Math.max(1, Math.round(archivo.size / 1024))} KB`;
  }

  protected async enviar(): Promise<void> {
    this.error.set(null);
    this.formulario.markAllAsTouched();

    const exigeDocumento = this.requiereDocumento();
    const archivo = this.documento();
    if (exigeDocumento && !archivo) {
      this.errorDocumento.set('Este motivo exige adjuntar un documento.');
    }
    if (this.formulario.invalid || (exigeDocumento && (!archivo || this.errorDocumento()))) {
      return;
    }

    const v = this.formulario.getRawValue();
    const solicitud: SolicitudAutorizacion = {
      propertyId: v.propiedadId,
      visitReasonId: v.motivoId,
      authorizationStartDate: new Date(v.inicio).toISOString(),
      authorizationEndDate: new Date(v.fin).toISOString(),
      observation: v.observacion.trim(),
      visitor: {
        documentNumber: v.documento.trim(),
        firstName: v.primerNombre.trim(),
        middleName: v.segundoNombre.trim(),
        lastName: v.primerApellido.trim(),
        secondLastName: v.segundoApellido.trim(),
        email: v.correo.trim().toLowerCase(),
        vehiclePlate: v.placa.trim().toUpperCase(),
      },
    };

    this.cargando.set(true);
    try {
      this.resultado.set(await this.servicio.autorizar(solicitud, exigeDocumento ? archivo : null));
    } catch (e) {
      this.error.set(this.mensajeDeError(e));
    } finally {
      this.cargando.set(false);
    }
  }

  protected nueva(): void {
    this.resultado.set(null);
    this.error.set(null);
    this.documento.set(null);
    this.errorDocumento.set(null);
    this.formulario.reset();
    this.formulario.patchValue(this.fechasIniciales());
  }

  protected volver(): Promise<boolean> {
    return this.router.navigate(['/inicio']);
  }

  private validarArchivo(archivo: File | null): string | null {
    if (!archivo) return null;
    if (!TIPOS_PERMITIDOS.includes(archivo.type)) return 'El documento debe ser PDF, JPG o PNG.';
    if (archivo.size > TAMANO_MAXIMO_DOCUMENTO) return 'El documento no puede superar 5 MB.';
    return null;
  }

  private mensajeDeError(e: unknown): string {
    if (e instanceof HttpErrorResponse && e.status !== 0) {
      if (e.status === 401) return 'Tu sesión expiró. Vuelve a iniciar sesión.';
      if (e.status === 403) return 'No tienes permiso para autorizar visitas en esta propiedad.';
      const crudo = (e.error as { messages?: string[] } | null)?.messages?.[0];
      if (crudo) {
        return MENSAJES[crudo.replace(/^USER_MESSAGE_+/, '')] ?? crudo;
      }
    }
    return this.auth.traducirError(e);
  }

  private formatoLegible(local: string | undefined): string {
    const fecha = new Date(local ?? '');
    return Number.isNaN(fecha.getTime())
      ? '—'
      : fecha.toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
  }

  private duracionTexto(inicio: string | undefined, fin: string | undefined): string | null {
    const ms = new Date(fin ?? '').getTime() - new Date(inicio ?? '').getTime();
    if (Number.isNaN(ms) || ms <= 0) return null;
    if (ms > DURACION_MAXIMA_MS) return 'Más de 24 h';
    const minutos = Math.round(ms / 60000);
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    if (horas === 0) return `${resto} min`;
    return resto === 0 ? `${horas} h` : `${horas} h ${resto} min`;
  }

  /** Inicio en 5 minutos y fin 2 horas después, en el formato del input datetime-local. */
  private fechasIniciales(): { inicio: string; fin: string } {
    const inicio = new Date(Date.now() + 5 * 60 * 1000);
    const fin = new Date(inicio.getTime() + 2 * 60 * 60 * 1000);
    return { inicio: this.formatoLocal(inicio), fin: this.formatoLocal(fin) };
  }

  private formatoLocal(fecha: Date): string {
    const dos = (n: number) => String(n).padStart(2, '0');
    return (
      `${fecha.getFullYear()}-${dos(fecha.getMonth() + 1)}-${dos(fecha.getDate())}` +
      `T${dos(fecha.getHours())}:${dos(fecha.getMinutes())}`
    );
  }
}
