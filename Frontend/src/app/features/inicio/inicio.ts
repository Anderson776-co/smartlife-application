import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss',
})
export class Inicio {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected async salir(): Promise<void> {
    await this.auth.cerrarSesion();
    await this.router.navigate(['/login']);
  }
}