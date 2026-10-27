import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly verContrasena = signal(false);

  protected readonly formulario = this.fb.nonNullable.group({
    correo: ['', [Validators.required, Validators.email]],
    contrasena: ['', [Validators.required]],
  });

  protected async enviar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);
    const { correo, contrasena } = this.formulario.getRawValue();

    try {
      await this.auth.iniciarSesion(correo, contrasena);
      await this.router.navigate(['/inicio']);
    } catch (e) {
      this.error.set(this.auth.traducirError(e));
    } finally {
      this.cargando.set(false);
    }
  }
}