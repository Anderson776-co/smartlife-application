import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Auth } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { Inicio } from './inicio';

describe('Inicio', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Inicio],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Auth, useValue: {} },
      ],
    }).compileComponents();
  });

  const mostrar = async (roles: string[]) => {
    TestBed.inject(AuthService).sesion.set({ userId: '1', email: 'usuario@example.com', roles });
    const fixture = TestBed.createComponent(Inicio);
    await fixture.whenStable();
    return fixture.nativeElement as HTMLElement;
  };

  it('muestra "Autorizar visita" al propietario', async () => {
    const pagina = await mostrar(['OWNER']);
    expect(pagina.textContent).toContain('Autorizar visita');
    expect(pagina.textContent).toContain('Propietario');
  });

  it('muestra "Autorizar visita" al residente', async () => {
    const pagina = await mostrar(['RESIDENT']);
    expect(pagina.textContent).toContain('Autorizar visita');
  });

  it('no muestra "Autorizar visita" a quien no es propietario ni residente', async () => {
    const pagina = await mostrar(['ADMIN']);
    expect(pagina.textContent).not.toContain('Autorizar visita');
  });
});
