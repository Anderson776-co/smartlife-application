import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-pie',
  imports: [RouterLink],
  templateUrl: './pie.html',
  styleUrl: './pie.scss',
})
export class Pie {
  private readonly auth = inject(AuthService);

  protected readonly anio = new Date().getFullYear();
  protected readonly puedeAutorizar = computed(() => this.auth.tieneRol('RESIDENT'));
}
