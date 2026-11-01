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

export interface AutorizacionCreada {
  authorizationId: string;
  qrUrl: string;
  notificationSent: boolean;
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
}
