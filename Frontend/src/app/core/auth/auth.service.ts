import { Injectable, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  Auth,
  User,
  authState,
  createUserWithEmailAndPassword,
  sendEmailVerification,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
  signOut,
  updateProfile,
} from '@angular/fire/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly auth = inject(Auth);

  /** Usuario de Firebase: undefined mientras carga, null si no hay sesión. */
  readonly usuario = toSignal(authState(this.auth));
  readonly estaAutenticado = computed(() => !!this.usuario());

  async iniciarSesion(correo: string, contrasena: string): Promise<User> {
    const credencial = await signInWithEmailAndPassword(this.auth, correo, contrasena);
    return credencial.user;
  }

  async crearCuenta(nombre: string, correo: string, contrasena: string): Promise<User> {
    const credencial = await createUserWithEmailAndPassword(this.auth, correo, contrasena);
    await updateProfile(credencial.user, { displayName: nombre });
    await sendEmailVerification(credencial.user);
    return credencial.user;
  }

  recuperarContrasena(correo: string): Promise<void> {
    return sendPasswordResetEmail(this.auth, correo);
  }

  cerrarSesion(): Promise<void> {
    return signOut(this.auth);
  }

  /** Token JWT que se enviará al backend en el encabezado Authorization. */
  async obtenerToken(): Promise<string | null> {
    const actual = this.auth.currentUser;
    return actual ? actual.getIdToken() : null;
  }

  /** Convierte los códigos de error de Firebase en mensajes para el usuario. */
  traducirError(error: unknown): string {
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