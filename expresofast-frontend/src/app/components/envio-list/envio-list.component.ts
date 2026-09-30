import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { EnvioService } from '../../services/envio.service';
import { Envio, EstadoEnvio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-list.component.html',
  styleUrl: './envio-list.component.css'
})
export class EnvioListComponent implements OnInit {
  private envioService = inject(EnvioService);

  envios = signal<Envio[]>([]);
  cargando = signal(true);
  error = signal('');

  readonly estados: EstadoEnvio[] = ['PENDIENTE', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO'];

  ngOnInit(): void {
    this.cargarEnvios();
  }

  cargarEnvios(): void {
    this.cargando.set(true);
    this.error.set('');
    this.envioService.obtenerEnvios().subscribe({
      next: (data) => {
        this.envios.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los envíos. Verifique que el backend esté corriendo.');
        this.cargando.set(false);
      }
    });
  }

  onCambiarEstado(envio: Envio, nuevoEstado: string): void {
    const estado = nuevoEstado as EstadoEnvio;
    this.envioService.actualizarEstado(envio.id, estado).subscribe({
      next: (actualizado) => {
        this.envios.update(lista =>
          lista.map(e => e.id === envio.id ? { ...e, estado: actualizado.estado } : e)
        );
      },
      error: () => {
        this.error.set(`No se pudo actualizar el estado del envío ${envio.codigoRastreo}.`);
      }
    });
  }

  claseEstado(estado: EstadoEnvio): string {
    return 'badge badge-' + estado.toLowerCase().replace('_', '-');
  }
}