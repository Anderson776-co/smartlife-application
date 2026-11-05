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
  authorizationStartDate: string;
  authorizationEndDate: string;
  observation: string;
  visitor: VisitanteSolicitud;
}

export interface PropiedadAutorizable {
  id: string;
  location: string;
}

export interface MotivoVisita {
  id: string;
  name: string;
  requiresDocument: boolean;
}

export interface AutorizacionCreada {
  authorizationId: string;
  qrUrl: string;
  notificationSent: boolean;
}

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

  async autorizar(
    solicitud: SolicitudAutorizacion,
    documento: File | null,
  ): Promise<AutorizacionCreada> {
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

  async listarPropiedades(): Promise<PropiedadAutorizable[]> {
    const respuesta = await firstValueFrom(
      this.http.get<RespuestaApi<PropiedadAutorizable>>(`${API_URL}/visits/properties`),
    );
    return respuesta.data;
  }

  async listarMotivos(): Promise<MotivoVisita[]> {
    const respuesta = await firstValueFrom(
      this.http.get<RespuestaApi<MotivoVisita>>(`${API_URL}/visits/reasons`),
    );
    return respuesta.data;
  }

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