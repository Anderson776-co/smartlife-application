import { Component, input, output, signal } from '@angular/core';

import { ResumenVisita } from '../autorizar-visita/resumen-visita';
import { AutorizacionCreada } from '../visit-authorization.service';

/** Partículas de la celebración: hacia dónde salen (x, y), giro, color y retraso. */
const CONFETI = [
  { x: -150, y: -120, r: 200, c: '#005ff9', d: 0 },
  { x: -90, y: -170, r: -160, c: '#13b57a', d: 40 },
  { x: -30, y: -190, r: 120, c: '#ffb400', d: 80 },
  { x: 40, y: -180, r: -220, c: '#005ff9', d: 20 },
  { x: 100, y: -160, r: 180, c: '#ff5a7a', d: 60 },
  { x: 160, y: -110, r: -140, c: '#13b57a', d: 100 },
  { x: -170, y: -50, r: 160, c: '#ffb400', d: 30 },
  { x: 175, y: -45, r: -200, c: '#005ff9', d: 70 },
  { x: -130, y: 10, r: -120, c: '#ff5a7a', d: 50 },
  { x: 135, y: 15, r: 240, c: '#ffb400', d: 10 },
];

@Component({
  selector: 'app-visita-autorizada',
  templateUrl: './visita-autorizada.html',
  styleUrl: './visita-autorizada.scss',
})
export class VisitaAutorizada {
  readonly resultado = input.required<AutorizacionCreada>();
  readonly resumen = input.required<ResumenVisita>();

  readonly otra = output<void>();
  readonly volver = output<void>();
  readonly verMisAutorizaciones = output<void>();

  protected readonly confeti = CONFETI;
  protected readonly copiado = signal(false);

  protected async copiar(): Promise<void> {
    try {
      await navigator.clipboard.writeText(this.resultado().authorizationId);
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2000);
    } catch {
      // Sin permiso para el portapapeles: el código se puede copiar a mano
    }
  }
}
