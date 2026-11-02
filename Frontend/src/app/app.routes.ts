import { Routes } from '@angular/router';

import { authGuard, rolGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    loadComponent: () => import('./features/auth/registro/registro').then((m) => m.Registro),
  },
  {
    path: 'recuperar',
    loadComponent: () => import('./features/auth/recuperar/recuperar').then((m) => m.Recuperar),
  },
  {
    path: 'inicio',
    canActivate: [authGuard],
    loadComponent: () => import('./features/inicio/inicio').then((m) => m.Inicio),
  },
  {
    path: 'visitas/autorizar',
    canActivate: [rolGuard('OWNER', 'RESIDENT')],
    loadComponent: () =>
      import('./features/visitas/autorizar-visita/autorizar-visita').then((m) => m.AutorizarVisita),
  },
  {
    path: 'visitas/mis-autorizaciones',
    canActivate: [rolGuard('OWNER', 'RESIDENT')],
    loadComponent: () =>
      import('./features/visitas/mis-autorizaciones/mis-autorizaciones').then(
        (m) => m.MisAutorizaciones,
      ),
  },
  { path: '**', redirectTo: 'login' },
];
