import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'inicio',
    loadComponent: () => import('./features/inicio/inicio').then((m) => m.Inicio),
  },
  { path: '**', redirectTo: 'login' },
];