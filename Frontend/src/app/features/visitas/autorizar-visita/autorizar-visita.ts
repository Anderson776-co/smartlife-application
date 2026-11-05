import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { Encabezado } from '../../../shared/encabezado/encabezado';
import { Pie } from '../../../shared/pie/pie';
import { Opcion } from '../catalogos';
import {
  AutorizacionCreada,
  SolicitudAutorizacion,
  VisitAuthorizationService,
} from '../visit-authorization.service';
import { VisitaAutorizada } from '../visita-autorizada/visita-autorizada';
import { ResumenVisita } from './resumen-visita';

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

  protected readonly propiedades = signal<Opcion[]>([]);
  protected readonly sinPropiedades = signal(false);
  protected readonly motivos = signal<Opcion[]>([]);

  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly resultado = signal<AutorizacionCreada | null>(null);
  protected readonly documento = signal<File | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    propiedadId: [''],
    motivoId: [''],
    inicio: [''],
    fin: [''],
    observacion: [''],
    documento: [''],
    primerNombre: [''],
    segundoNombre: [''],
    primerApellido: [''],
    segundoApellido: [''],
    correo: [''],
    placa: [''],
  });

  private readonly motivoSeleccionado = toSignal(this.formulario.controls.motivoId.valueChanges, {
    initialValue: '',
  });

  protected readonly requiereDocumento = computed(
    () => this.motivos().find((m) => m.id === this.motivoSeleccionado())?.requiereDocumento ?? false,
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
      propiedad: this.propiedades().find((p) => p.id === v.propiedadId)?.nombre ?? null,
      motivo: this.motivos().find((m) => m.id === v.motivoId)?.nombre ?? null,
      inicio: this.formatoLegible(v.inicio),
      fin: this.formatoLegible(v.fin),
      duracion: this.duracion(),
      visitante: nombre || null,
      documento: (v.documento ?? '').trim() || null,
      correo: (v.correo ?? '').trim().toLowerCase() || null,
    };
  });

  protected readonly progreso = computed(() => {
    this.valores();
    const c = this.formulario.controls;
    const principales = [
      c.propiedadId,
      c.motivoId,
      c.inicio,
      c.fin,
      c.documento,
      c.primerNombre,
      c.primerApellido,
      c.correo,
    ];
    let hechos = principales.filter((campo) => campo.value.trim().length > 0).length;
    let total = principales.length;
    if (this.requiereDocumento()) {
      total += 1;
      if (this.documento()) hechos += 1;
    }
    return Math.round((hechos / total) * 100);
  });

  constructor() {
    this.formulario.patchValue(this.fechasIniciales());
    void this.cargarPropiedades();
    void this.cargarMotivos();
  }

  private async cargarPropiedades(): Promise<void> {
    try {
      const propiedades = await this.servicio.listarPropiedades();
      this.propiedades.set(propiedades.map((p) => ({ id: p.id, nombre: p.location })));
      this.sinPropiedades.set(propiedades.length === 0);
      if (propiedades.length === 1) {
        this.formulario.controls.propiedadId.setValue(propiedades[0].id);
      }
    } catch (e) {
      this.error.set(this.mensajeDeError(e));
    }
  }

  private async cargarMotivos(): Promise<void> {
    try {
      const motivos = await this.servicio.listarMotivos();
      this.motivos.set(
        motivos.map((m) => ({ id: m.id, nombre: m.name, requiereDocumento: m.requiresDocument })),
      );
    } catch (e) {
      this.error.set(this.mensajeDeError(e));
    }
  }

  protected elegirArchivo(evento: Event): void {
    const archivo = (evento.target as HTMLInputElement).files?.[0] ?? null;
    this.documento.set(archivo);
  }

  protected quitarArchivo(entrada: HTMLInputElement): void {
    entrada.value = '';
    this.documento.set(null);
  }

  protected tamano(archivo: File): string {
    return archivo.size >= 1024 * 1024
      ? `${(archivo.size / (1024 * 1024)).toFixed(1)} MB`
      : `${Math.max(1, Math.round(archivo.size / 1024))} KB`;
  }

  protected async enviar(): Promise<void> {
    this.error.set(null);

    const archivo = this.requiereDocumento() ? this.documento() : null;

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
      this.resultado.set(await this.servicio.autorizar(solicitud, archivo));
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
    this.formulario.reset();
    this.formulario.patchValue(this.fechasIniciales());
  }

  protected verMisAutorizaciones(): Promise<boolean> {
    return this.router.navigate(['/visitas/mis-autorizaciones']);
  }

  protected volver(): Promise<boolean> {
    return this.router.navigate(['/inicio']);
  }

  private mensajeDeError(e: unknown): string {
    if (e instanceof HttpErrorResponse && e.status !== 0) {
      const mensaje = (e.error as { messages?: string[] } | null)?.messages?.[0];
      if (mensaje) return mensaje;
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
    const minutos = Math.round(ms / 60000);
    const horas = Math.floor(minutos / 60);
    const resto = minutos % 60;
    if (horas === 0) return `${resto} min`;
    return resto === 0 ? `${horas} h` : `${horas} h ${resto} min`;
  }

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