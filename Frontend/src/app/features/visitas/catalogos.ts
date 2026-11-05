/**
 * LISTA TEMPORAL PARA LA DEMO.
 * El backend todavía no expone GET para los motivos de visita, así que se usan los datos de
 * prueba de la base (05-datosprueba.sql). Las propiedades ya NO están aquí: se piden al API
 * (GET /visits/properties) y solo devuelven las viviendas del usuario en sesión.
 */
export interface Opcion {
  id: string;
  nombre: string;
  requiereDocumento?: boolean;
}

export const MOTIVOS: Opcion[] = [
  { id: 'ffffffff-0000-0000-0000-000000000002', nombre: 'Visita familiar', requiereDocumento: false },
  { id: 'ffffffff-0000-0000-0000-000000000005', nombre: 'Servicio técnico', requiereDocumento: true },
];
