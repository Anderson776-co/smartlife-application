import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Auth } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';
import { vi } from 'vitest';

import { AuthService } from '../../../core/auth/auth.service';
import { VisitAuthorizationService } from '../visit-authorization.service';
import { AutorizarVisita } from './autorizar-visita';

const CASA_101 = 'cccccccc-0000-0000-0000-000000000001';
const VISITA_FAMILIAR = 'ffffffff-0000-0000-0000-000000000002';
const SERVICIO_TECNICO = 'ffffffff-0000-0000-0000-000000000005';

describe('AutorizarVisita', () => {
  const autorizar = vi.fn();

  beforeEach(async () => {
    autorizar.mockReset();
    autorizar.mockResolvedValue({
      authorizationId: 'abc-123',
      qrUrl: 'http://localhost:4200/visitas/autorizaciones/abc-123',
      notificationSent: true,
    });

    await TestBed.configureTestingModule({
      imports: [AutorizarVisita],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Auth, useValue: {} },
        { provide: VisitAuthorizationService, useValue: { autorizar } },
      ],
    }).compileComponents();
  });

  const crear = async (roles: string[]) => {
    TestBed.inject(AuthService).sesion.set({ userId: '1', email: 'carlos@example.com', roles });
    const fixture = TestBed.createComponent(AutorizarVisita);
    await fixture.whenStable();
    const formulario = (
      fixture.componentInstance as unknown as {
        formulario: { patchValue: (valor: object) => void };
      }
    ).formulario;
    return { fixture, formulario, pagina: fixture.nativeElement as HTMLElement };
  };

  const visitanteValido = {
    documento: 'AB1234',
    primerNombre: 'Luis',
    primerApellido: 'Torres',
    correo: 'LUIS@Example.com',
    placa: 'abc123',
  };

  it('se muestra igual para el propietario (Carlos)', async () => {
    const { pagina } = await crear(['OWNER']);
    expect(pagina.querySelector('h1')?.textContent).toContain('Autorizar visita');
    expect(pagina.querySelectorAll('form select').length).toBe(2);
  });

  it('envía la autorización con los datos normalizados', async () => {
    const { fixture, formulario, pagina } = await crear(['OWNER']);
    formulario.patchValue({ propiedadId: CASA_101, motivoId: VISITA_FAMILIAR, ...visitanteValido });
    fixture.detectChanges();

    (pagina.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();
    fixture.detectChanges();

    expect(autorizar).toHaveBeenCalledTimes(1);
    const [solicitud, documento] = autorizar.mock.calls[0];
    expect(solicitud.propertyId).toBe(CASA_101);
    expect(solicitud.visitReasonId).toBe(VISITA_FAMILIAR);
    expect(solicitud.visitor.email).toBe('luis@example.com');
    expect(solicitud.visitor.vehiclePlate).toBe('ABC123');
    expect(new Date(solicitud.authorizationEndDate).getTime()).toBeGreaterThan(
      new Date(solicitud.authorizationStartDate).getTime(),
    );
    expect(documento).toBeNull();
    expect(pagina.textContent).toContain('¡Visita autorizada!');
    expect(pagina.textContent).toContain('abc-123');
  });

  it('no valida en el navegador: envía aunque falten datos y deja que el backend responda', async () => {
    autorizar.mockRejectedValue(
      new HttpErrorResponse({ status: 400, error: { messages: ['Mensaje del catálogo'] } }),
    );
    const { fixture, formulario, pagina } = await crear(['RESIDENT']);
    formulario.patchValue({ propiedadId: CASA_101, motivoId: SERVICIO_TECNICO });
    fixture.detectChanges();
    expect(pagina.querySelector('input[type="file"]')).not.toBeNull();

    (pagina.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();
    fixture.detectChanges();

    expect(autorizar).toHaveBeenCalledTimes(1);
    expect(autorizar.mock.calls[0][1]).toBeNull();
    expect(pagina.querySelector('[role="alert"]')?.textContent).toContain('Mensaje del catálogo');
  });

  it('envía duraciones largas sin bloquearlas: la regla es del backend', async () => {
    const { fixture, formulario, pagina } = await crear(['OWNER']);
    const pad = (n: number) => String(n).padStart(2, '0');
    const local = (f: Date) =>
      `${f.getFullYear()}-${pad(f.getMonth() + 1)}-${pad(f.getDate())}T${pad(f.getHours())}:${pad(f.getMinutes())}`;
    const inicio = new Date(Date.now() + 10 * 60 * 1000);
    const fin = new Date(inicio.getTime() + 30 * 60 * 60 * 1000);
    formulario.patchValue({
      propiedadId: CASA_101,
      motivoId: VISITA_FAMILIAR,
      inicio: local(inicio),
      fin: local(fin),
      ...visitanteValido,
    });
    fixture.detectChanges();

    (pagina.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();
    fixture.detectChanges();

    expect(autorizar).toHaveBeenCalledTimes(1);
  });

  it('muestra el mensaje del backend también cuando responde 403', async () => {
    autorizar.mockRejectedValue(
      new HttpErrorResponse({ status: 403, error: { messages: ['Sin acceso según el catálogo'] } }),
    );
    const { fixture, formulario, pagina } = await crear(['OWNER']);
    formulario.patchValue({ propiedadId: CASA_101, motivoId: VISITA_FAMILIAR, ...visitanteValido });
    fixture.detectChanges();

    (pagina.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();
    fixture.detectChanges();

    expect(pagina.querySelector('[role="alert"]')?.textContent).toContain(
      'Sin acceso según el catálogo',
    );
  });
});
