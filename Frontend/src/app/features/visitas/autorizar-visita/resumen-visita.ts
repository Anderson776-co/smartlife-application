/** Datos del formulario ya listos para mostrarse en el resumen y en la pantalla de éxito. */
export interface ResumenVisita {
  propiedad: string | null;
  motivo: string | null;
  inicio: string;
  fin: string;
  duracion: string | null;
  visitante: string | null;
  documento: string | null;
  correo: string | null;
}
