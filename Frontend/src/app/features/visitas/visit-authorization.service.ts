import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { API_URL } from '../../core/api/api-config';

export interface VisitanteSolicitud {
  documentNumber: string;
  firstName: string;
  middleName: string;
  lastName: string;
  secondLastName: string;
  email: string;
  vehiclePlate: string;
}

export interface SolicitudAutorizacion {
  propertyId: string;
  visitReasonId: string;
  /** Fecha y hora en formato ISO 8601 (UTC). */
  authorizationStartDate: string;
  authorizationEndDate: string;
  observation: string;
  visitor: VisitanteSolicitud;
}

/** Una vivienda sobre la que el usuario en sesión puede autorizar visitas (GET /visits/properties). */
export interface PropiedadAutorizable {
  propertyId: string;
  location: string;
}

export interface AutorizacionCreada {
  authorizationId: string;
  qrUrl: string;
  notificationSent: boolean;
}

/** Una autorización ya registrada, tal como la devuelve GET /visits/authorizations. `isActive` es el de la base de datos. */
export interface AutorizacionResumen {
  authorizationId: string;
  authorizationStartDate: string;
  authorizationEndDate: string;
  propertyLocation: string;
  visitReason: string;
  documentAttached: boolean;
  visitorDocumentNumber: string;
  visitorFullName: string;
  visitorEmail: string;
  vehiclePlate: string;
  isActive: boolean;
}

/** Una página de resultados (los números de página empiezan en 0, como en el backend). */
export interface PaginaAutorizaciones {
  content: AutorizacionResumen[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

interface RespuestaApi<T> {
  messages: string[];
  data: T[];
  responseSucceded: boolean;
}

@Injectable({ providedIn: 'root' })
export class VisitAuthorizationService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/visits/authorizations`;

  /**
   * Envía la autorización como multipart: la parte "authorization" lleva el JSON
   * y la parte "document" (opcional) lleva el archivo.
   */
  async autorizar(
    solicitud: SolicitudAutorizacion,
    documento: File | null,
  ): Promise<AutorizacionCreada> {
    // Deja la cookie XSRF-TOKEN al día; Angular la envía como encabezado X-XSRF-TOKEN
    await firstValueFrom(this.http.get(`${API_URL}/users/sessions/csrf`));

    const cuerpo = new FormData();
    cuerpo.append(
      'authorization',
      new Blob([JSON.stringify(solicitud)], { type: 'application/json' }),
    );
    if (documento) {
      cuerpo.append('document', documento, documento.name);
    }

    const respuesta = await firstValueFrom(
      this.http.post<RespuestaApi<AutorizacionCreada>>(this.url, cuerpo),
    );
    const creada = respuesta.data[0];
    if (!creada) {
      throw new Error('El servidor no devolvió la autorización creada.');
    }
    return creada;
  }

  /** Solo las viviendas del usuario en sesión (solo donde es residente); el backend filtra. */
  async listarPropiedades(): Promise<PropiedadAutorizable[]> {
    const respuesta = await firstValueFrom(
      this.http.get<RespuestaApi<PropiedadAutorizable>>(`${API_URL}/visits/properties`),
    );
    return respuesta.data;
  }

  /**
   * Una página de las autorizaciones que hizo el usuario en sesión, de la más reciente a la más antigua
   * (GET /visits/authorizations?page=0&size=10, con `isActive` opcional). La paginación la hace el
   * servidor: la respuesta trae `data[0]` con `content`, `page`, `size`, `totalElements` y `totalPages`.
   */
  async listar(
    pagina: number,
    tamano: number,
    activa?: boolean,
  ): Promise<PaginaAutorizaciones> {
    const params: Record<string, string | number | boolean> = { page: pagina, size: tamano };
    if (activa !== undefined) params['isActive'] = activa;
    const respuesta = await firstValueFrom(
      this.http.get<RespuestaApi<PaginaAutorizaciones>>(this.url, { params }),
    );
    const resultado = respuesta.data[0];
    if (!resultado) {
      throw new Error('El servidor no devolvió la lista de autorizaciones.');
    }
    return resultado;
  }
}
