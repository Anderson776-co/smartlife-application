import { Routes } from '@angular/router';

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
    loadComponent: () => import('./features/inicio/inicio').then((m) => m.Inicio),
  },
  { path: '**', redirectTo: 'login' },
];