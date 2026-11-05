import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';
import { Encabezado } from '../../../shared/encabezado/encabezado';
import { Paginador } from '../../../shared/paginador/paginador';
import { Pie } from '../../../shared/pie/pie';
import {
  AutorizacionResumen,
  PaginaAutorizaciones,
  VisitAuthorizationService,
} from '../visit-authorization.service';

const TAMANO_POR_DEFECTO = 10;
const TAMANOS = [5, 10, 20];

/** Lo que se ve en la insignia: sale de `isActive` y de las fechas, el backend ya no lo calcula. */
type EstadoVisual = 'CURRENT' | 'UPCOMING' | 'EXPIRED' | 'CANCELLED';

const ETIQUETAS: Record<EstadoVisual, string> = {
  CURRENT: 'Vigente',
  UPCOMING: 'Programada',
  EXPIRED: 'Vencida',
  CANCELLED: 'Cancelada',
};

/** Filtro de la lista: se traduce a `isActive=true|false` en la petición. */
type FiltroEstado = 'ACTIVAS' | 'CANCELADAS';

const FILTROS: Record<FiltroEstado, { etiqueta: string; activa: boolean }> = {
  ACTIVAS: { etiqueta: 'Activas', activa: true },
  CANCELADAS: { etiqueta: 'Canceladas', activa: false },
};

@Component({
  selector: 'app-mis-autorizaciones',
  imports: [RouterLink, Encabezado, Pie, Paginador],
  templateUrl: './mis-autorizaciones.html',
  styleUrl: './mis-autorizaciones.scss',
})
export class MisAutorizaciones {
  private readonly servicio = inject(VisitAuthorizationService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly ruta = inject(ActivatedRoute);

  protected readonly tamanos = TAMANOS;
  protected readonly estados = (Object.keys(FILTROS) as FiltroEstado[]).map((valor) => ({
    valor,
    etiqueta: FILTROS[valor].etiqueta,
  }));

  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  /** La página que devolvió el servidor (solo trae los elementos de esa página). */
  protected readonly resultado = signal<PaginaAutorizaciones | null>(null);

  /** Estado, página y tamaño viven en la URL (?estado=CANCELADAS&pagina=3&tamano=10): sirven "atrás" y recargar. */
  private readonly params = toSignal(this.ruta.queryParamMap, { requireSync: true });
  protected readonly filtroEstado = computed<FiltroEstado | ''>(() => {
    const valor = this.params().get('estado');
    return valor && valor in FILTROS ? (valor as FiltroEstado) : '';
  });
  protected readonly numeroPagina = computed(() => {
    const n = Number(this.params().get('pagina'));
    return Number.isInteger(n) && n >= 1 ? n - 1 : 0;
  });
  protected readonly tamano = computed(() => {
    const n = Number(this.params().get('tamano'));
    return TAMANOS.includes(n) ? n : TAMANO_POR_DEFECTO;
  });

  protected readonly items = computed(() => this.resultado()?.content ?? []);
  protected readonly vacio = computed(
    () => !this.cargando() && !this.error() && this.resultado()?.totalElements === 0,
  );

  private peticion = 0;

  constructor() {
    // Cada cambio de página, tamaño o estado pide al servidor solo la página que se va a mostrar.
    effect(() => {
      void this.cargar(this.numeroPagina(), this.tamano(), this.filtroEstado());
    });
  }

  protected async cargar(
    pagina = this.numeroPagina(),
    tamano = this.tamano(),
    estado: FiltroEstado | '' = this.filtroEstado(),
  ): Promise<void> {
    const actual = ++this.peticion; // ignora respuestas viejas si el usuario cambia rápido de página
    this.cargando.set(true);
    this.error.set(null);
    try {
      const respuesta = await this.servicio.listar(
        pagina,
        tamano,
        estado ? FILTROS[estado].activa : undefined,
      );
      if (actual !== this.peticion) return;
      // Si la página pedida ya no existe (p. ej. ?pagina=99), vuelve a la última que sí existe.
      if (respuesta.totalPages > 0 && pagina >= respuesta.totalPages) {
        await this.navegar({ pagina: respuesta.totalPages === 1 ? null : respuesta.totalPages });
        return;
      }
      this.resultado.set(respuesta);
    } catch (e) {
      if (actual !== this.peticion) return;
      this.resultado.set(null);
      this.error.set(this.mensajeDeError(e));
    } finally {
      if (actual === this.peticion) this.cargando.set(false);
    }
  }

  protected irAPagina(pagina: number): Promise<boolean> {
    return this.navegar({ pagina: pagina === 0 ? null : pagina + 1 });
  }

  protected cambiarTamano(tamano: number): Promise<boolean> {
    // Al cambiar el tamaño se vuelve a la primera página
    return this.navegar({ pagina: null, tamano: tamano === TAMANO_POR_DEFECTO ? null : tamano });
  }

  protected cambiarEstado(evento: Event): Promise<boolean> {
    const valor = (evento.target as HTMLSelectElement).value;
    return this.navegar({ pagina: null, estado: valor || null });
  }

  protected estadoVisual(a: AutorizacionResumen): EstadoVisual {
    if (!a.isActive) return 'CANCELLED';
    const ahora = Date.now();
    if (ahora < new Date(a.authorizationStartDate).getTime()) return 'UPCOMING';
    if (ahora > new Date(a.authorizationEndDate).getTime()) return 'EXPIRED';
    return 'CURRENT';
  }

  protected etiqueta(estado: EstadoVisual): string {
    return ETIQUETAS[estado];
  }

  protected fecha(iso: string): string {
    return new Date(iso).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
  }

  protected codigoCorto(id: string): string {
    return id.slice(0, 8).toUpperCase();
  }

  private navegar(queryParams: Record<string, string | number | null>): Promise<boolean> {
    return this.router.navigate([], {
      relativeTo: this.ruta,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }

  private mensajeDeError(e: unknown): string {
    if (e instanceof HttpErrorResponse && e.status !== 0) {
      const mensaje = (e.error as { messages?: string[] } | null)?.messages?.[0];
      if (mensaje) return mensaje;
    }
    return this.auth.traducirError(e);
  }
}
