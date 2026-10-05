import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, debounceTime, map, switchMap, take } from 'rxjs/operators';

import { EnvioService } from '../services/envio.service';


export function trackingDisponibleValidator(envioService: EnvioService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    if (!control.value) {
      return of(null);
    }

    return of(control.value).pipe(
      debounceTime(400), 
      switchMap(valor => envioService.checkTracking(valor)),
      map(resultado => (resultado.existe ? { trackingTomado: true } : null)),
      catchError(() => of(null)), 
      take(1)
    );
  };
}
