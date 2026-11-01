const NOMBRES_ROL: Record<string, string> = {
  OWNER: 'Propietario',
  RESIDENT: 'Residente',
  ADMIN: 'Administrador',
};

/** Nombre del rol para mostrar en pantalla. */
export function nombreDeRol(rol: string): string {
  return NOMBRES_ROL[rol] ?? rol;
}
