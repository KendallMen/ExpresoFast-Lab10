import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { EnvioService } from '../../services/envio.service';
import { fechaEntregaPosteriorADespachoValidator } from '../../validators/fechas.validator';
import { trackingDisponibleValidator } from '../../validators/tracking.validator';


type PaqueteForm = ReturnType<EnvioAvanzadoFormComponent['crearPaqueteForm']>;

@Component({
  selector: 'app-envio-avanzado-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './envio-avanzado-form.component.html',
  styleUrl: './envio-avanzado-form.component.css'
})
export class EnvioAvanzadoFormComponent {
  private fb = inject(NonNullableFormBuilder);
  private envioService = inject(EnvioService);
  private router = inject(Router);

  enviando = signal(false);
  mensajeExito = signal('');
  error = signal('');

  form = this.fb.group(
    {
      numeroTracking: this.fb.control('', {
        validators: [Validators.required, Validators.minLength(4)],
        asyncValidators: [trackingDisponibleValidator(this.envioService)],
        updateOn: 'blur' // evita disparar la consulta HTTP en cada tecla
      }),
      destinatario: this.fb.control('', Validators.required),
      direccionDestino: this.fb.control('', Validators.required),
      montoFlete: this.fb.control(0, [Validators.required, Validators.min(1)]),
      fechaDespacho: this.fb.control('', Validators.required),
      fechaEntregaEstimada: this.fb.control('', Validators.required),
      paquetes: this.fb.array([this.crearPaqueteForm()])
    },
    { validators: fechaEntregaPosteriorADespachoValidator() }
  );

  get paquetesArray() {
    return this.form.controls.paquetes;
  }

  private crearPaqueteForm() {
    return this.fb.group({
      descripcion: this.fb.control('', Validators.required),
      pesoKg: this.fb.control(0, [Validators.required, Validators.min(0.01)])
    });
  }

  agregarPaquete(): void {
    this.paquetesArray.push(this.crearPaqueteForm());
  }

  eliminarPaquete(indice: number): void {
    if (this.paquetesArray.length <= 1) {
      return; 
    }
    this.paquetesArray.removeAt(indice);
  }

  get numeroTrackingControl(): FormControl<string> {
    return this.form.controls.numeroTracking;
  }

  onSubmit(): void {
    if (this.form.invalid || this.form.pending) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.error.set('');
    this.mensajeExito.set('');

    const valores = this.form.getRawValue();

    this.envioService.registrarEnvioCompleto(valores).subscribe({
      next: (creado) => {
        this.enviando.set(false);
        this.mensajeExito.set(`Envío ${creado.codigoRastreo} registrado con ${valores.paquetes.length} paquete(s).`);
        setTimeout(() => this.router.navigate(['/envios']), 1500);
      },
      error: (err) => {
        this.enviando.set(false);
        this.error.set(err?.error?.mensaje ?? 'No se pudo registrar el envío. Intente de nuevo.');
      }
    });
  }
}
