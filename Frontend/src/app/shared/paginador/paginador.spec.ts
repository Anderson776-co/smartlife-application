import { TestBed } from '@angular/core/testing';

import { Paginador, paginasVisibles } from './paginador';

describe('paginasVisibles', () => {
  it('muestra todas si son pocas', () => {
    expect(paginasVisibles(0, 1)).toEqual([0]);
    expect(paginasVisibles(3, 7)).toEqual([0, 1, 2, 3, 4, 5, 6]);
  });

  it('abrevia con puntos al inicio, al medio y al final', () => {
    expect(paginasVisibles(0, 20)).toEqual([0, 1, 2, 3, null, 19]);
    expect(paginasVisibles(9, 20)).toEqual([0, null, 8, 9, 10, null, 19]);
    expect(paginasVisibles(19, 20)).toEqual([0, null, 16, 17, 18, 19]);
  });
});

describe('Paginador', () => {
  const crear = (pagina: number, total = 37, tamano = 10) => {
    const fixture = TestBed.createComponent(Paginador);
    fixture.componentRef.setInput('pagina', pagina);
    fixture.componentRef.setInput('tamano', tamano);
    fixture.componentRef.setInput('totalElementos', total);
    fixture.componentRef.setInput('totalPaginas', Math.ceil(total / tamano));
    fixture.detectChanges();
    return fixture;
  };

  it('resume el rango mostrado', () => {
    const texto = (crear(3).nativeElement as HTMLElement).querySelector('.resumen')?.textContent;
    expect(texto?.replace(/\s+/g, ' ')).toContain('31–37 de 37');
  });

  it('deshabilita "Anterior" en la primera página y "Siguiente" en la última', () => {
    const primera = (crear(0).nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>('.flecha');
    expect(primera[0].disabled).toBe(true);
    expect(primera[1].disabled).toBe(false);
    const ultima = (crear(3).nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>('.flecha');
    expect(ultima[1].disabled).toBe(true);
  });

  it('emite la página elegida (base 0) y no emite la actual', () => {
    const fixture = crear(1);
    const emitidas: number[] = [];
    fixture.componentInstance.cambiarPagina.subscribe((p) => emitidas.push(p));
    const botones = (fixture.nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>('.numero');
    botones[0].click(); // página 1 → índice 0
    botones[1].click(); // actual → no emite
    expect(emitidas).toEqual([0]);
  });

  it('emite el nuevo tamaño', () => {
    const fixture = crear(0);
    const tamanos: number[] = [];
    fixture.componentInstance.cambiarTamano.subscribe((t) => tamanos.push(t));
    const select = (fixture.nativeElement as HTMLElement).querySelector('select') as HTMLSelectElement;
    select.value = '20';
    select.dispatchEvent(new Event('change'));
    expect(tamanos).toEqual([20]);
  });
});
