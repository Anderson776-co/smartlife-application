# Vistas materializadas y eventos entre módulos

Cada módulo guarda en su propio esquema una **vista materializada** de los datos que necesita de otro módulo
(por ejemplo, `visits.authorizator` copia a los usuarios). Cuando esos datos cambian, la vista tiene que
ponerse al día. Esto se hace de dos formas que conviven:

1. **Base de datos** (`db/init/03b-refresco-automatico-vistas.sql`): triggers que refrescan las vistas ante
   cualquier cambio, venga de donde venga (scripts, SQL manual). Es la red de seguridad.
2. **Aplicación** (este documento): el módulo dueño del dato publica un **evento** y cada módulo que tiene una
   vista lo escucha y refresca **solo sus vistas**. Es la forma que respeta los límites entre módulos de
   Spring Modulith: quien cambia el dato no conoce a quien lo escucha.

Los dos refrescos son idempotentes: refrescar dos veces deja la vista igual.

## Flujo

```
PATCH (ADMIN) -> Controller -> InputPort -> InputAdapter -> UseCase (sin retorno)
                                              |  DTO request -> Domain (MapStruct)
                                              v
               UseCase: lee Entity del repositorio -> Domain (mapper), cambia el Domain,
                        Domain -> Entity (mapper) -> Repository.update(...)
                        Domain -> Evento (mapper) -> DomainEventPublisherOutputPort.publish(...)
                                                          |  dentro de la transaccion
                                                          v
        Listener del modulo con la vista (@TransactionalEventListener + REQUIRES_NEW, corre cuando la transaccion ya se confirmo)
                     Evento -> DTO request (mapper) -> InputPort -> InputAdapter
                     DTO request -> Domain (mapper) -> UseCase (sin retorno)
                                                          |
                              MaterializedViewRefresherOutputPort.refresh(vista)
                                                          |
                              REFRESH MATERIALIZED VIEW CONCURRENTLY <vista>
```

### Mapeo entre capas

| De | A | Donde | Clase |
|---|---|---|---|
| DTO request | Domain | InputAdapter | `*MapperInput` |
| Domain | Entity (repositorio) y Entity | UseCase | `*MapperUseCase` (`DomainMapper`) |
| Domain | Evento (publicacion hacia otro modulo) | UseCase | `*EventMapperUseCase` |
| Evento | DTO request | Listener | `*EventMapperInput` |
| Entity | JPA | Repositorio | mappers que ya existian (`EntityMapper`) |

Los dos casos de uso (el que actualiza el dato y el que refresca la vista) **no devuelven nada**
(`UseCaseWithoutReturn`), y por eso los puertos son `InputPortWithOutReturn`: el controlador solo responde el sobre
de exito, igual que el logout. Los objetos de dominio son inmutables (`withActive`, `withOwner`, `withName`).

## Endpoints (solo rol ADMIN)

| Módulo | Endpoint | Cuerpo | Evento |
|---|---|---|---|
| users | `PATCH /api/v1/users/{userId}/status` | `{"active": true}` | `UserUpdatedEvent` |
| urbanization | `PATCH /api/v1/urbanizations/{urbanizationId}` | `{"name": "Nuevo nombre"}` | `UrbanizationUpdatedEvent` |
| dwellings | `PATCH /api/v1/dwellings/{dwellingId}/owner` | `{"ownerId": "<uuid>"}` | `DwellingUpdatedEvent` |
| residents | `PATCH /api/v1/residents/residences/{residentResidenceId}/status` | `{"active": false}` | `ResidentResidenceUpdatedEvent` |

Los eventos viven en el paquete `event` de cada módulo (interfaz nombrada `events`), que es lo único que los
otros módulos pueden usar de él.

## Quién escucha qué

| Evento | Módulo que escucha | Vistas que refresca |
|---|---|---|
| `UserUpdatedEvent` | dwellings | `dwellings.user_owner` |
| | residents | `residents.user_resident` |
| | visits | `visits.authorizator` |
| `UrbanizationUpdatedEvent` | users | `users.urbanization` |
| | dwellings | `dwellings.urbanization` |
| `DwellingUpdatedEvent` | residents | `residents.residence` |
| | visits | `visits.property_visit` |
| `ResidentResidenceUpdatedEvent` | visits | `visits.resident` |

Dependencias entre módulos (declaradas en el `package-info.java` de cada uno, sin ciclos):
`users → urbanization`, `dwellings → users, urbanization`, `residents → users, dwellings`,
`visits → users, dwellings, residents`.

## Mensajes nuevos para el catálogo de Strapi

Cada código necesita dos mensajes: `USER_MESSAGE_<código>` y `TECHNICAL_MESSAGE_<código>`.

`USER_NOT_FOUND`, `USER_STATUS_REQUIRED`, `URBANIZATION_NOT_FOUND`, `URBANIZATION_NAME_INVALID`,
`DWELLING_NOT_FOUND`, `DWELLING_OWNER_NOT_FOUND`, `DWELLING_OWNER_NOT_ACTIVE`,
`DWELLING_OWNER_OTHER_URBANIZATION`, `RESIDENT_RESIDENCE_NOT_FOUND`, `RESIDENT_RESIDENCE_STATUS_REQUIRED`.

## Cómo probarlo en local

1. Hace falta un usuario con rol `ADMIN` (los datos de prueba solo traen OWNER y RESIDENT). Por ejemplo, para
   darle ADMIN a Carlos:
   ```sql
   INSERT INTO users.user_role (id_user, id_role) VALUES
       ('bbbbbbbb-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000001');
   ```
2. Iniciar sesión como ese usuario (cookie de sesión) y pedir el token CSRF (`GET /api/v1/users/sessions/csrf`).
3. Cambiar un dato, por ejemplo desactivar a María, enviando el encabezado `X-XSRF-TOKEN`:
   `PATCH /api/v1/users/bbbbbbbb-0000-0000-0000-000000000002/status` con `{"active": false}`.
4. Comprobar que la vista quedó al día **sin refrescar a mano**:
   ```sql
   SELECT id, is_active FROM visits.authorizator WHERE id = 'bbbbbbbb-0000-0000-0000-000000000002';
   ```
   Y, en la aplicación, que María ya no puede autorizar visitas (403).

## Notas

- Los listeners usan `@TransactionalEventListener` (despues de confirmar) con `@Transactional(REQUIRES_NEW)`, que es lo que
  hace `@ApplicationModuleListener` de Spring Modulith sin la parte asincrona. Son sincronos: al responder el `PATCH`,
  las vistas ya estan al dia. Para hacerlos asincronos habria que agregar `@Async` y `@EnableAsync`.
- Si una vista no se puede refrescar, el error queda en el log y el cambio original se conserva; los triggers de
  la base de datos la ponen al día en la siguiente escritura.
- No se usa el registro de publicación de eventos de Spring Modulith (JPA/JDBC): si la aplicación se cae
  justo entre guardar y refrescar, no se reintenta. Para eso están los triggers.
