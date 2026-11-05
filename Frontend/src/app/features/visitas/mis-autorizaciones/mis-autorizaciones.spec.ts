import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Auth } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { AuthService } from '../../../core/auth/auth.service';
import { PaginaAutorizaciones } from '../visit-authorization.service';
import { MisAutorizaciones } from './mis-autorizaciones';

const URL = '/api/v1/visits/authorizations';

/** Lo que devolvería el backend para la página pedida de un total de `total` autorizaciones. */
const pagina = (page: number, size: number, total: number): PaginaAutorizaciones => ({
  content: Array.from({ length: Math.max(Math.min(size, total - page * size), 0) }, (_, i) => ({
    authorizationId: `id-${page}-${i}-00000000`,
    authorizationStartDate: '2026-01-01T10:00:00Z',
    authorizationEndDate: '2026-01-01T12:00:00Z',
    propertyLocation: 'Casa 101 - Bloque A',
    visitReason: 'Visita familiar',
    documentAttached: false,
    visitorDocumentNumber: '123',
    visitorFullName: `Visitante ${page * size + i + 1}`,
    visitorEmail: 'v@example.com',
    vehiclePlate: '',
    isActive: true,
  })),
  page,
  size,
  totalElements: total,
  totalPages: Math.ceil(total / size),
});

describe('MisAutorizaciones', () => {
  let http: HttpTestingController;
  let arnes: RouterTestingHarness;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'mis', component: MisAutorizaciones }]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Auth, useValue: {} },
      ],
    }).compileComponents();
    TestBed.inject(AuthService).sesion.set({ userId: '1', email: 'maria@example.com', roles: ['RESIDENT'] });
    http = TestBed.inject(HttpTestingController);
    arnes = await RouterTestingHarness.create();
  });

  /** Deja que la promesa del servicio termine y repinta la pantalla. */
  const asentar = async () => {
    await new Promise((resolver) => setTimeout(resolver));
    arnes.detectChanges();
  };
  const texto = () => (arnes.routeNativeElement as HTMLElement).textContent!.replace(/\s+/g, ' ');
  /** Nombres de los visitantes que se ven en la lista (uno por tarjeta). */
  const nombres = () =>
    Array.from((arnes.routeNativeElement as HTMLElement).querySelectorAll('article h3')).map(
      (h) => h.textContent!.trim(),
    );
  const responder = async (datos: PaginaAutorizaciones) => {
    http.expectOne((r) => r.url === URL).flush({ messages: [], responseSucceded: true, data: [datos] });
    await asentar();
  };

  it('pide al servidor solo la primera página (page=0&size=10) y la muestra', async () => {
    await arnes.navigateByUrl('/mis', MisAutorizaciones);
    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.has('isActive')).toBe(false);
    req.flush({ messages: [], responseSucceded: true, data: [pagina(0, 10, 23)] });
    await asentar();

    expect(nombres()).toHaveLength(10);
    expect(nombres()[0]).toBe('Visitante 1');
    expect(nombres()[9]).toBe('Visitante 10');
    expect(texto()).toContain('1–10 de 23');
    expect(texto()).toContain('23 en total');
  });

  it('al pasar de página vuelve a pedir al servidor la página siguiente', async () => {
    await arnes.navigateByUrl('/mis', MisAutorizaciones);
    await responder(pagina(0, 10, 23));

    (arnes.routeNativeElement as HTMLElement)
      .querySelector<HTMLButtonElement>('button[aria-label="Página siguiente"]')!
      .click();
    await asentar();

    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('page')).toBe('1');
    req.flush({ messages: [], responseSucceded: true, data: [pagina(1, 10, 23)] });
    await asentar();
    expect(nombres()[0]).toBe('Visitante 11');
    expect(texto()).toContain('11–20 de 23');
  });

  it('lee página y tamaño de la URL (?pagina=3&tamano=5 → page=2&size=5)', async () => {
    await arnes.navigateByUrl('/mis?pagina=3&tamano=5', MisAutorizaciones);
    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('5');
    req.flush({ messages: [], responseSucceded: true, data: [pagina(2, 5, 23)] });
    await asentar();
    expect(nombres()).toEqual(['Visitante 11', 'Visitante 12', 'Visitante 13', 'Visitante 14', 'Visitante 15']);
  });

  it('si la página pedida ya no existe, vuelve a la última', async () => {
    await arnes.navigateByUrl('/mis?pagina=99', MisAutorizaciones);
    await responder(pagina(98, 10, 23)); // el servidor devuelve el contenido vacío con los totales reales
    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('page')).toBe('2');
    req.flush({ messages: [], responseSucceded: true, data: [pagina(2, 10, 23)] });
    await asentar();
    expect(texto()).toContain('21–23 de 23');
  });

  it('manda el filtro elegido al backend (?estado=CANCELADAS → isActive=false)', async () => {
    await arnes.navigateByUrl('/mis?estado=CANCELADAS', MisAutorizaciones);
    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('isActive')).toBe('false');
    req.flush({ messages: [], responseSucceded: true, data: [pagina(0, 10, 2)] });
  });

  it('calcula la insignia con isActive y las fechas', async () => {
    const hora = 3600_000;
    const desde = (h: number) => new Date(Date.now() + h * hora).toISOString();
    const base = pagina(0, 10, 4);
    const [a, b, c, d] = base.content;
    base.content = [
      { ...a, visitorFullName: 'Programada', authorizationStartDate: desde(1), authorizationEndDate: desde(2) },
      { ...b, visitorFullName: 'Vigente', authorizationStartDate: desde(-1), authorizationEndDate: desde(1) },
      { ...c, visitorFullName: 'Vencida', authorizationStartDate: desde(-3), authorizationEndDate: desde(-2) },
      { ...d, visitorFullName: 'Cancelada', isActive: false, authorizationStartDate: desde(-1), authorizationEndDate: desde(1) },
    ];
    await arnes.navigateByUrl('/mis', MisAutorizaciones);
    await responder(base);

    const chips = Array.from(
      (arnes.routeNativeElement as HTMLElement).querySelectorAll('.estado-chip'),
    ).map((chip) => chip.textContent!.trim());
    expect(chips).toEqual(['Programada', 'Vigente', 'Vencida', 'Cancelada']);
  });

  it('manda isActive=true al elegir las activas (?estado=ACTIVAS)', async () => {
    await arnes.navigateByUrl('/mis?estado=ACTIVAS', MisAutorizaciones);
    const req = http.expectOne((r) => r.url === URL);
    expect(req.request.params.get('isActive')).toBe('true');
    req.flush({ messages: [], responseSucceded: true, data: [pagina(0, 10, 1)] });
  });

  it('muestra el mensaje del backend si falla', async () => {
    await arnes.navigateByUrl('/mis', MisAutorizaciones);
    http
      .expectOne((r) => r.url === URL)
      .flush(
        { messages: ['Sin permiso para consultar.'], responseSucceded: false, data: [] },
        { status: 403, statusText: 'Forbidden' },
      );
    await asentar();
    expect(texto()).toContain('Sin permiso para consultar.');
  });

  it('muestra el estado vacío', async () => {
    await arnes.navigateByUrl('/mis', MisAutorizaciones);
    await responder(pagina(0, 10, 0));
    expect(texto()).toContain('Aún no has autorizado visitas');
  });
});
