import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { EnvioService } from '../../services/envio.service';
import { Envio, EstadoEnvio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-tracking.component.html',
  styleUrl: './envio-tracking.component.css'
})
export class EnvioTrackingComponent {
  private envioService = inject(EnvioService);

  codigo = '';
  envio = signal<Envio | null>(null);
  buscando = signal(false);
  error = signal('');

  private ordenEstados: EstadoEnvio[] = ['PENDIENTE', 'EN_TRANSITO', 'ENTREGADO'];

  buscar(): void {
    if (!this.codigo.trim()) {
      this.error.set('Ingrese un código de rastreo.');
      return;
    }

    this.buscando.set(true);
    this.error.set('');
    this.envio.set(null);

    this.envioService.obtenerPorRastreo(this.codigo.trim()).subscribe({
      next: (data) => {
        this.envio.set(data);
        this.buscando.set(false);
      },
      error: () => {
        this.error.set(`No se encontró ningún envío con el código "${this.codigo}".`);
        this.buscando.set(false);
      }
    });
  }

  porcentajeProgreso(): number {
    const envio = this.envio();
    if (!envio) return 0;
    if (envio.estado === 'CANCELADO') return 100;
    const indice = this.ordenEstados.indexOf(envio.estado);
    if (indice === -1) return 0;
    return ((indice + 1) / this.ordenEstados.length) * 100;
  }
}