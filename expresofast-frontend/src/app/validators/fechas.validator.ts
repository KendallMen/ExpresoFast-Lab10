import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';


export function fechaEntregaPosteriorADespachoValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const despacho = group.get('fechaDespacho')?.value;
    const entrega = group.get('fechaEntregaEstimada')?.value;

    if (!despacho || !entrega) {
      return null; 
    }

    const fechaDespacho = new Date(despacho);
    const fechaEntrega = new Date(entrega);

    return fechaEntrega > fechaDespacho ? null : { fechaInvalida: true };
  };
}
