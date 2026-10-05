import { Paquete } from './paquete.model';

export interface EnvioRegistro {
  numeroTracking: string;
  destinatario: string;
  direccionDestino: string;
  montoFlete: number;
  fechaDespacho: string;
  fechaEntregaEstimada: string;
  paquetes: Paquete[];
}

export interface TrackingDisponible {
  numeroTracking: string;
  existe: boolean;
}
