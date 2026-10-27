import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

/** Solo deja pasar a quien tiene sesión; si no, lo manda al login. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.estaAutenticado() ? true : router.createUrlTree(['/login']);
};

/** Además de tener sesión, exige alguno de los roles indicados (ej. 'OWNER', 'RESIDENT'). */
export const rolGuard =
  (...roles: string[]): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.estaAutenticado()) {
      return router.createUrlTree(['/login']);
    }
    return auth.tieneRol(...roles) ? true : router.createUrlTree(['/inicio']);
  };