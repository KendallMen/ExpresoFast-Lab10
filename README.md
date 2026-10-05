# ExpresoFast — Migración a SPA con Angular Standalone

**Universidad de Costa Rica — Sede del Atlántico, Recinto Paraíso**
**Carrera de Informática Empresarial**
**Curso:** IF0009 - Desarrollo de Software IV
**Profesor:** Mag. Jonathan Granados C.
**Ciclo:** II-2026
**Laboratorio:** 10 — Migración a Frontend SPA con Angular Standalone y Backend RESTful por Capas
**Estudiante:** Kendall Méndez Calderón
**Carné:** C4H142

---

## Descripción

Migración del cliente web de ExpresoFast a una **Single Page Application** en
**Angular Standalone**, consumiendo una API RESTful de **Spring Boot**
estructurada por capas (dominio, repositorio, DTOs, servicio y controlador).
Permite listar envíos, registrar nuevas guías y rastrear un envío por su
código, actualizando el estado sin recargar la página.

## Estructura del repositorio

```
expresofast-project/
├── expresofast-backend/     Proyecto Spring Boot (Java 21, H2 en memoria)
│   └── src/main/java/com/expresofast/
│       ├── model/            Envio.java, EstadoEnvio.java
│       ├── repository/       EnvioRepository.java
│       ├── dto/               EnvioDTO.java, CrearEnvioDTO.java, ActualizarEstadoDTO.java
│       ├── service/           EnvioService.java, EnvioServiceImpl.java
│       ├── controller/        EnvioController.java
│       └── config/            DataSeeder.java
│
└── expresofast-frontend/    Proyecto Angular Standalone
    └── src/
        ├── environments/      environment.ts
        └── app/
            ├── models/         envio.model.ts
            ├── services/       envio.service.ts
            ├── components/
            │   ├── envio-list/
            │   ├── envio-form/
            │   └── envio-tracking/
            ├── app.config.ts
            └── app.routes.ts
```

## Requisitos de entorno

| Herramienta | Versión utilizada |
|---|---|
| Java | 21+ |
| Maven | última versión |
| Node.js | 18+ |
| Angular CLI | 22.x |
| Navegador | Google Chrome / Firefox (con DevTools) |

No se requiere SQL Server ni ninguna instalación de base de datos: el backend
usa **H2 en memoria**, que se crea automáticamente al arrancar.

## 1. Ejecutar el backend (API REST)

```cmd
cd expresofast-backend
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080/api/v1/envios`.

Al arrancar, `DataSeeder` inserta automáticamente 5 envíos de ejemplo
(`EXP-2026-1001` a `EXP-2026-1005`) con distintos estados
(PENDIENTE, EN_TRANSITO, ENTREGADO, CANCELADO), solo si la tabla está vacía.

> **Nota:** al ser una base en memoria, los datos se reinician cada vez que
> se detiene el proceso. No es necesario ni recomendable detener el backend
> entre pruebas si se quiere conservar información registrada manualmente.

### Consola H2 (para verificar persistencia)

1. Con el backend corriendo, abre `http://localhost:8080/h2-console`.
2. JDBC URL: `jdbc:h2:mem:expresofast`
3. User Name: `sa` — Password: (vacío)
4. Clic en **Connect** y ejecuta `SELECT * FROM envio;` para ver los datos reales.

## 2. Ejecutar el frontend (Angular)

```cmd
cd expresofast-frontend
npm install
ng serve
```

Abre `http://localhost:4200`. La ruta por defecto redirige a `/envios`.

| Ruta | Componente | Descripción |
|---|---|---|
| `/envios` | `EnvioListComponent` | Tabla de guías con insignia de color por estado y selector para actualizar el estado directamente. |
| `/nuevo-envio` | `EnvioFormComponent` | Formulario de registro de un nuevo envío (código de rastreo generado automáticamente por el backend). |
| `/rastreo` | `EnvioTrackingComponent` | Búsqueda por código de rastreo con ficha detallada y barra de progreso. |

> **Nota sobre zoneless change detection:** este proyecto Angular (generado
> con Angular CLI 22) no incluye `zone.js`. Por eso el estado que se
> actualiza dentro de callbacks asíncronos (`subscribe()` de `HttpClient`) se
> maneja con **signals** (`signal()`) en los tres componentes, en vez de
> propiedades planas, para que Angular detecte los cambios y vuelva a pintar
> la vista automáticamente.

## 3. Endpoints de la API

Todos bajo la ruta base `/api/v1/envios`. CORS habilitado explícitamente para
`http://localhost:4200` mediante `@CrossOrigin` en `EnvioController`.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/envios` | Lista todos los envíos. |
| GET | `/api/v1/envios/rastreo/{codigo}` | Detalle de un envío por su código de rastreo. |
| POST | `/api/v1/envios` | Registra un nuevo envío. Genera el código `EXP-{año}-{4 dígitos}` automáticamente. |
| PATCH | `/api/v1/envios/{id}/estado` | Actualiza el estado de un envío. |

### Ejemplo — Registrar un envío

```
POST http://localhost:8080/api/v1/envios
Content-Type: application/json

{
  "destinatario": "Ana Mora",
  "direccionDestino": "Heredia, Barva",
  "montoFlete": 3200.0
}
```

### Ejemplo — Actualizar estado

```
PATCH http://localhost:8080/api/v1/envios/1/estado
Content-Type: application/json

{ "estado": "EN_TRANSITO" }
```

Valores válidos de `estado`: `PENDIENTE`, `EN_TRANSITO`, `ENTREGADO`, `CANCELADO`.


### Nueva funcionalidad

- **Relación 1:N Envio–Paquete**, mapeada con `@OneToMany`/`@ManyToOne` y
  `cascade = ALL` + `orphanRemoval = true`, de forma que el envío y todos sus
  paquetes se guardan (o se quitan) en una sola transacción
  (`@Transactional` en `EnvioServiceImpl.registrarCompleto`).
- **Endpoint nuevo:** `GET /api/v1/envios/check-tracking/{trackingNumber}`,
  usado por el validador asíncrono de Angular.
- **Endpoint nuevo:** `POST /api/v1/envios/completo`, recibe el envío junto
  con la lista de paquetes (`EnvioRegistroDTO`).
- **Componente Angular `EnvioAvanzadoFormComponent`** (ruta `/envio-avanzado`):
  formulario reactivo **estrictamente tipado** (`NonNullableFormBuilder`),
  con un `FormArray` dinámico de paquetes, validación cruzada de fechas y
  validación asíncrona del número de rastreo.

### Fundamentación teórica

**1. UX y escalabilidad: `FormArray` vs. campos estáticos**

FormArray permite agregar y eliminar paquetes según lo que el usuario
necesite. Cada elemento del arreglo representa un paquete y utiliza las mismas
reglas de validación para la descripción y el peso. Angular actualiza la vista
cuando se agrega o elimina un elemento, por lo que el usuario no tiene que
trabajar con diez campos visibles u ocultos ni con un límite fijo.

También mejora el mantenimiento del código. Con diez campos estáticos habría
que repetir el HTML, validar cada campo por separado y filtrar manualmente los
campos vacíos antes de enviar el formulario. Con FormArray, la cantidad de
controles coincide con la cantidad de paquetes y todos se envían juntos en el
JSON. Si después cambia la estructura de un paquete, la modificación se hace
en un solo formulario y no en diez bloques repetidos.

**2. Ciclo de eventos: validadores síncronos y asíncronos**

El validador de fechas es síncrono porque solo usa valores que ya están en el
formulario. Su comparación se ejecuta directamente en el call stack y Angular
recibe el resultado en ese mismo momento: el formulario queda válido o
inválido sin esperar otra operación.

El validador de tracking es asíncrono porque debe hacer una petición HTTP al
backend. La solicitud se procesa fuera del call stack y, cuando llega la
respuesta, el navegador ejecuta el callback mediante el Event Loop. Mientras
espera, Angular mantiene el control en estado PENDING.

Angular necesita que un validador asíncrono retorne un Observable o una
Promise porque el resultado no está disponible de inmediato. Estos objetos
representan una operación futura y permiten que Angular se suscriba, espere la
respuesta y actualice el estado del control cuando la operación termine, sin
bloquear la interfaz.