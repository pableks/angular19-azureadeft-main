export interface SignosVitales {
  pacienteId: string;
  frecuenciaCardiaca: number;
  presionArterial: string;  // Should be string, not number
  temperatura: number;
  saturacionOxigeno: number;
  timestamp: string;
}