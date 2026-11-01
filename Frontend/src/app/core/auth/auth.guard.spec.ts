import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Auth } from '@angular/fire/auth';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

import { rolGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('rolGuard(OWNER, RESIDENT)', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Auth, useValue: {} },
      ],
    });
  });

  const evaluar = () =>
    TestBed.runInInjectionContext(() =>
      rolGuard(
        'OWNER',
        'RESIDENT',
      )({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );

  const iniciarSesion = (roles: string[]) =>
    TestBed.inject(AuthService).sesion.set({ userId: '1', email: 'usuario@example.com', roles });

  it('deja pasar a un propietario (Carlos)', () => {
    iniciarSesion(['OWNER']);
    expect(evaluar()).toBe(true);
  });

  it('deja pasar a un residente (María)', () => {
    iniciarSesion(['RESIDENT']);
    expect(evaluar()).toBe(true);
  });

  it('manda a /inicio a quien tiene otro rol', () => {
    iniciarSesion(['ADMIN']);
    expect((evaluar() as UrlTree).toString()).toBe('/inicio');
  });

  it('manda a /login a quien no tiene sesión', () => {
    TestBed.inject(AuthService).sesion.set(null);
    expect((evaluar() as UrlTree).toString()).toBe('/login');
  });
});
