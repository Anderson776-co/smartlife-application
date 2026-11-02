import { Component, computed, input, output } from '@angular/core';

/** Números de página a mostrar; `null` es un "…". Ej.: 1 … 4 5 [6] 7 8 … 20 */
export function paginasVisibles(actual: number, total: number): (number | null)[] {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i);
  const ultima = total - 1;
  const cerca = new Set([0, ultima, actual - 1, actual, actual + 1]);
  if (actual <= 2) [1, 2, 3].forEach((p) => cerca.add(p));
  if (actual >= ultima - 2) [ultima - 3, ultima - 2, ultima - 1].forEach((p) => cerca.add(p));
  const orden = [...cerca].filter((p) => p >= 0 && p <= ultima).sort((a, b) => a - b);
  const salida: (number | null)[] = [];
  orden.forEach((p, i) => {
    if (i > 0 && p - orden[i - 1] > 1) salida.push(null);
    salida.push(p);
  });
  return salida;
}

@Component({
  selector: 'app-paginador',
  templateUrl: './paginador.html',
  styleUrl: './paginador.scss',
})
export class Paginador {
  /** Página actual, empezando en 0. */
  readonly pagina = input.required<number>();
  readonly tamano = input.required<number>();
  readonly totalElementos = input.required<number>();
  readonly totalPaginas = input.required<number>();
  readonly opcionesTamano = input<number[]>([5, 10, 20]);
  readonly deshabilitado = input(false);

  readonly cambiarPagina = output<number>();
  readonly cambiarTamano = output<number>();

  protected readonly paginas = computed(() => paginasVisibles(this.pagina(), this.totalPaginas()));
  protected readonly desde = computed(() =>
    this.totalElementos() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );
  protected readonly hasta = computed(() =>
    Math.min((this.pagina() + 1) * this.tamano(), this.totalElementos()),
  );

  protected ir(destino: number): void {
    if (this.deshabilitado() || destino < 0 || destino >= this.totalPaginas()) return;
    if (destino !== this.pagina()) this.cambiarPagina.emit(destino);
  }

  protected elegirTamano(evento: Event): void {
    this.cambiarTamano.emit(Number((evento.target as HTMLSelectElement).value));
  }
}
