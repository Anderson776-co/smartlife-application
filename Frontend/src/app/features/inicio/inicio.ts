import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { nombreDeRol } from '../../core/auth/roles';
import { Encabezado } from '../../shared/encabezado/encabezado';
import { Pie } from '../../shared/pie/pie';

@Component({
  selector: 'app-inicio',
  imports: [Encabezado, Pie],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss',
})
export class Inicio {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** Propietarios y residentes pueden autorizar visitas. */
  protected readonly puedeAutorizar = computed(() => this.auth.tieneRol('OWNER', 'RESIDENT'));

  protected readonly rol = computed(() => this.auth.roles().map(nombreDeRol).join(' · '));

  /** "carlos@example.com" → "Carlos" */
  protected readonly nombre = computed(() => {
    const primera = (this.auth.sesion()?.email ?? '').split('@')[0].split(/[._-]/)[0];
    return primera ? primera.charAt(0).toUpperCase() + primera.slice(1) : '';
  });

  protected irAutorizarVisita(): Promise<boolean> {
    return this.router.navigate(['/visitas/autorizar']);
  }
}
