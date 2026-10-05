import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CrearEnvioPayload, Envio, EstadoEnvio } from '../models/envio.model';
import { EnvioRegistro, TrackingDisponible } from '../models/envio-registro.model';

@Injectable({
  providedIn: 'root'
})
export class EnvioService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl + 'envios';

    checkTracking(numeroTracking: string): Observable<TrackingDisponible> {
    return this.http.get<TrackingDisponible>(`${this.baseUrl}/check-tracking/${numeroTracking}`);
  }

  registrarEnvioCompleto(payload: EnvioRegistro): Observable<Envio> {
    return this.http.post<Envio>(`${this.baseUrl}/completo`, payload);
  }

  obtenerEnvios(): Observable<Envio[]> {
    return this.http.get<Envio[]>(this.baseUrl);
  }

  obtenerPorRastreo(codigo: string): Observable<Envio> {
    return this.http.get<Envio>(`${this.baseUrl}/rastreo/${codigo}`);
  }

  crearEnvio(payload: CrearEnvioPayload): Observable<Envio> {
    return this.http.post<Envio>(this.baseUrl, payload);
  }

  actualizarEstado(id: number, nuevoEstado: EstadoEnvio): Observable<Envio> {
    return this.http.patch<Envio>(`${this.baseUrl}/${id}/estado`, { estado: nuevoEstado });
  }
}
