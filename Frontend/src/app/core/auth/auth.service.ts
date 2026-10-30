import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import {
  Auth,
  createUserWithEmailAndPassword,
  sendEmailVerification,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
  signOut,
  updateProfile,
} from '@angular/fire/auth';
import { firstValueFrom } from 'rxjs';

import { API_URL } from '../api/api-config';

export interface Sesion {
  userId: string;
  email: string;
  roles: string[];
}

interface RespuestaApi<T> {
  messages: string[];
  data: T[];
  responseSucceded: boolean;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly auth = inject(Auth);
  private readonly http = inject(HttpClient);
  private readonly urlSesiones = `${API_URL}/users/sessions`;

  /** undefined = todavía no se sabe; null = no hay sesión. */
  readonly sesion = signal<Sesion | null | undefined>(undefined);
  readonly estaAutenticado = computed(() => !!this.sesion());
  readonly roles = computed(() => this.sesion()?.roles ?? []);

  tieneRol(...esperados: string[]): boolean {
    return esperados.some((rol) => this.roles().includes(rol));
  }

  /** Pregunta al backend quién es el usuario de la cookie de sesión. */
  async cargarSesion(): Promise<void> {
    try {
      const respuesta = await firstValueFrom(
        this.http.get<RespuestaApi<Sesion>>(`${this.urlSesiones}/me`),
      );
      this.sesion.set(respuesta.data[0] ?? null);
    } catch {
      this.sesion.set(null);
    }
  }

  /** Firebase valida correo y contraseña; el backend crea la sesión con el idToken. */
  async iniciarSesion(correo: string, contrasena: string): Promise<void> {
    const credencial = await signInWithEmailAndPassword(this.auth, correo, contrasena);
    try {
      const idToken = await credencial.user.getIdToken();
      await this.asegurarCsrf();
      await firstValueFrom(this.http.post(this.urlSesiones, { idToken }));
      await this.cargarSesion();
    } finally {
      // La sesión real es la cookie del backend; no dejamos a Firebase logueado en el navegador
      await signOut(this.auth);
    }
  }

  /** Crea la cuenta en Firebase. El registro en la base de datos lo hará el backend. */
  async crearCuenta(nombre: string, correo: string, contrasena: string): Promise<void> {
    const credencial = await createUserWithEmailAndPassword(this.auth, correo, contrasena);
    await updateProfile(credencial.user, { displayName: nombre });
    await sendEmailVerification(credencial.user);
    await signOut(this.auth);
  }

  recuperarContrasena(correo: string): Promise<void> {
    return sendPasswordResetEmail(this.auth, correo);
  }

  async cerrarSesion(): Promise<void> {
    try {
      await this.asegurarCsrf();
      await firstValueFrom(this.http.post(`${this.urlSesiones}/logout`, {}));
    } finally {
      this.sesion.set(null);
    }
  }

  /** El backend deja la cookie XSRF-TOKEN con esta llamada; Angular la envía como encabezado. */
  private asegurarCsrf(): Promise<unknown> {
    return firstValueFrom(this.http.get(`${this.urlSesiones}/csrf`));
  }

  /** Convierte errores de Firebase o del backend en mensajes para el usuario. */
  traducirError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return 'No se pudo conectar con el servidor. Inténtalo de nuevo.';
      }
      const mensaje = (error.error as Partial<RespuestaApi<unknown>> | null)?.messages?.[0];
      return mensaje ?? 'Ocurrió un error inesperado. Inténtalo de nuevo.';
    }

    const codigo = (error as { code?: string })?.code ?? '';
    switch (codigo) {
      case 'auth/invalid-credential':
      case 'auth/user-not-found':
      case 'auth/wrong-password':
        return 'Correo o contraseña incorrectos.';
      case 'auth/email-already-in-use':
        return 'Ya existe una cuenta con este correo.';
      case 'auth/weak-password':
        return 'La contraseña es muy débil. Usa al menos 6 caracteres.';
      case 'auth/invalid-email':
        return 'El correo no tiene un formato válido.';
      case 'auth/too-many-requests':
        return 'Demasiados intentos. Espera unos minutos e inténtalo de nuevo.';
      case 'auth/network-request-failed':
        return 'No hay conexión. Revisa tu internet e inténtalo de nuevo.';
      case 'auth/user-disabled':
        return 'Esta cuenta está deshabilitada.';
      default:
        return 'Ocurrió un error inesperado. Inténtalo de nuevo.';
    }
  }
}