import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { nombreDeRol } from '../../core/auth/roles';

@Component({
  selector: 'app-encabezado',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './encabezado.html',
  styleUrl: './encabezado.scss',
})
export class Encabezado {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly correo = computed(() => this.auth.sesion()?.email ?? '');
  protected readonly inicial = computed(() => this.correo().charAt(0).toUpperCase() || '?');
  protected readonly rol = computed(() => this.auth.roles().map(nombreDeRol).join(' · '));
  protected readonly puedeAutorizar = computed(() => this.auth.tieneRol('OWNER', 'RESIDENT'));

  protected async salir(): Promise<void> {
    await this.auth.cerrarSesion();
    await this.router.navigate(['/login']);
  }
}
