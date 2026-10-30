/**
 * LISTAS TEMPORALES PARA LA DEMO.
 * El backend todavía no expone GET para propiedades ni motivos de visita, así que se usan
 * los datos de prueba de la base (05-datosprueba.sql). Cuando existan los endpoints, se
 * reemplazan por llamadas al API y se borra este archivo.
 */
export interface Opcion {
  id: string;
  nombre: string;
  requiereDocumento?: boolean;
}

export const PROPIEDADES: Opcion[] = [
  { id: 'cccccccc-0000-0000-0000-000000000001', nombre: 'Casa 101 - Bloque A' },
  { id: 'cccccccc-0000-0000-0000-000000000002', nombre: 'Apartamento 202 - Bloque B' },
];

export const MOTIVOS: Opcion[] = [
  { id: 'ffffffff-0000-0000-0000-000000000002', nombre: 'Visita familiar', requiereDocumento: false },
  { id: 'ffffffff-0000-0000-0000-000000000005', nombre: 'Servicio técnico', requiereDocumento: true },
];
