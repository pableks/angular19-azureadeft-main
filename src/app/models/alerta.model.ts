export interface Alerta {
  id?: number;
  pacienteId?: number;
  pacienteNombre?: string;
  fecha: string;
  tipo: string;
  descripcion: string;
  timestamp?: string;
  leida?: boolean;
} 