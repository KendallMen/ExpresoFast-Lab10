import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { EnvioService } from '../../services/envio.service';
import { CrearEnvioPayload } from '../../models/envio.model';

@Component({
  selector: 'app-envio-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-form.component.html',
  styleUrl: './envio-form.component.css'
})
export class EnvioFormComponent {
  private envioService = inject(EnvioService);
  private router = inject(Router);

  datos: CrearEnvioPayload = {
    destinatario: '',
    direccionDestino: '',
    montoFlete: 0
  };

 
  enviando = signal(false);
  mensajeExito = signal('');
  error = signal('');

  onSubmit(): void {
    if (!this.datos.destinatario || !this.datos.direccionDestino || this.datos.montoFlete <= 0) {
      this.error.set('Complete todos los campos con valores válidos.');
      return;
    }

    this.enviando.set(true);
    this.error.set('');
    this.mensajeExito.set('');

    this.envioService.crearEnvio(this.datos).subscribe({
      next: (creado) => {
        this.enviando.set(false);
        this.mensajeExito.set(`Envío ${creado.codigoRastreo} registrado con éxito.`);
        this.datos = { destinatario: '', direccionDestino: '', montoFlete: 0 };
        setTimeout(() => this.router.navigate(['/envios']), 1500);
      },
      error: () => {
        this.enviando.set(false);
        this.error.set('No se pudo registrar el envío. Verifique los datos e intente de nuevo.');
      }
    });
  }
}