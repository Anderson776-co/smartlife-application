import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/auth/auth.service';

/** Valida que la contraseña y su confirmación sean iguales. */
function contrasenasIguales(grupo: AbstractControl): ValidationErrors | null {
  const contrasena = grupo.get('contrasena')?.value;
  const confirmar = grupo.get('confirmar')?.value;
  return contrasena === confirmar ? null : { noCoinciden: true };
}

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.scss',
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly verContrasena = signal(false);

  protected readonly formulario = this.fb.nonNullable.group(
    {
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(8)]],
      confirmar: ['', [Validators.required]],
    },
    { validators: contrasenasIguales },
  );

  protected async enviar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);
    const { nombre, correo, contrasena } = this.formulario.getRawValue();

    try {
      await this.auth.crearCuenta(nombre.trim(), correo.trim(), contrasena);
      await this.router.navigate(['/inicio']);
    } catch (e) {
      this.error.set(this.auth.traducirError(e));
    } finally {
      this.cargando.set(false);
    }
  }
}