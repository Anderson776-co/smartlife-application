# Secretos y variables de Cloud Run

Este documento explica cómo se guardan las claves de SmartLife en **Google Cloud Secret Manager**, cómo llegan a Cloud Run y dónde vive la base de datos. Las claves **nunca** se escriben en archivos del repositorio, en el chat ni en GitHub.

- Proyecto de Google Cloud: `smartlife-application` (ID `project-078c9c72-e891-42db-98c`)
- Servicio de Cloud Run: `smartlife-application`, región `us-central1`
- Cuenta de servicio que lee los secretos: `smartlife-runtime@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com`
- Rol dado a esa cuenta, solo sobre cada secreto: **Usuario con acceso a secretos de Secret Manager** (`roles/secretmanager.secretAccessor`)

## Secretos (van a Secret Manager)

Cada secreto está en una sola ubicación (`us-central1`) y con cifrado administrado por Google, para mantenerse dentro de lo gratuito.

| Variable que lee la aplicación | Secreto en Secret Manager | Estado |
|---|---|---|
| `RESEND_API_KEY` | `resend-api-key` | Con valor (versión 1) y conectado en el despliegue |
| `DB_PASSWORD` | `db-password` | Con valor y conectado en el despliegue. La **versión 3** es la contraseña del rol `smartlife_app` de Neon, y es la que se lee (`latest`). Las versiones 1 y 2 eran de una base anterior en Cloud SQL, que ya se eliminó: no se usan y se pueden inhabilitar |
| `STRAPI_API_TOKEN` | `strapi-api-token` | Creado, vacío: falta el token de Strapi |
| `R2_ACCESS_KEY` | `r2-access-key` | Creado, vacío: falta la cuenta de Cloudflare R2 |
| `R2_SECRET_KEY` | `r2-secret-key` | Creado, vacío: falta la cuenta de Cloudflare R2 |
| `REDIS_PASSWORD` | `redis-password` | Creado, vacío: falta el servidor de Redis |
| `FIREBASE_CREDENTIALS_BASE64` | No se usa | El adaptador usa las credenciales de la cuenta de servicio de Cloud Run |

## Base de datos (Neon)

La base de datos de SmartLife es PostgreSQL en **Neon**, plan gratuito, en la cuenta de Neon de Anderson. Antes se había creado una instancia en Cloud SQL, pero se eliminó: no se usa y no hace falta ningún conector de Google ni permiso de Cloud SQL.

- Proyecto de Neon: `smartlife`, rama `production`, región AWS US East 2 (Ohio).
- Base: `smartlife`. Rol de la aplicación: `smartlife_app`, dueño de la base.
- Host (conexión directa, sin pooler): `ep-round-frost-b4icbrl3.c-6.us-east-2.aws.neon.tech`. El host no es secreto; la contraseña sí y vive solo en `db-password`.
- `DB_URL`: `jdbc:postgresql://` + host + `/smartlife?sslmode=require`. El cifrado es obligatorio.
- Estructura: se creó con los scripts de `src/main/resources/db/init`, ejecutados en este orden con el rol `smartlife_app`: `01-esquemas.sql`, `02-tablas.sql` y `03-vistasmaterializadas.sql`. Resultado: 5 esquemas, 15 tablas y 8 vistas materializadas.
- La tabla `visits.visit_authorization` en Neon ya tiene la columna `is_active`, que usa el caso de uso de autorizar visita. El `02-tablas.sql` de esta rama todavía no la trae: viene en la rama `fix/authorize_visit_arranque`.
- Las vistas materializadas se refrescan a mano (`db/test/07-refrescarvistas.sql`): nada las refresca solas.
- Neon suspende la base cuando no se usa, y la primera consulta después de un rato tarda más. Conviene hacer una consulta antes de una demostración.
- Probado el 29 de octubre de 2026: conexión desde pgAdmin con el usuario `smartlife_app` y la contraseña de la versión 3 de `db-password`, con la estructura completa visible. Falta probar la conexión desde Cloud Run.

## Variables normales (no son secretas)

Van como variables de entorno del servicio de Cloud Run. Las tres primeras se definen en `.github/workflows/cicd.yml`; el resto usa los valores por defecto de `application.properties`:

| Variable | Valor |
|---|---|
| `DB_URL` | Dirección de Neon (ver arriba), definida en el `cicd.yml` |
| `DB_USER` | `smartlife_app`, definida en el `cicd.yml` |
| `CONFIG_SERVER_URL` | Dirección del servidor de parámetros en Cloud Run, definida en el `cicd.yml` (por defecto `http://localhost:8888`) |
| `DDL_AUTO` | `none` |
| `STRAPI_URL` | vacío |
| `RESEND_FROM` | `onboarding@resend.dev` |
| `R2_ENDPOINT`, `R2_BUCKET` | vacío |
| `FIREBASE_PROJECT_ID` | vacío |
| `CACHE_TYPE` | `none` (para activar Redis: `redis`) |
| `CACHE_TTL` | `1h` |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_SSL` | `localhost`, `6379`, vacío, `false` |
| `SESSION_DURATION`, `SESSION_COOKIE_NAME`, `SESSION_CHECK_REVOKED` | `5d`, `__Host-smartlife_session`, `true` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` |
| `QR_BASE_URL` | `http://localhost:5173/visitas/autorizaciones/` |

## Cómo se conecta un secreto

El paso `Deploy` de `.github/workflows/cicd.yml` usa la acción `google-github-actions/deploy-cloudrun`. Los secretos se listan en su opción `secrets`, con el formato `VARIABLE=nombre-del-secreto:versión`, y las variables normales en `env_vars`. Con `secrets_update_strategy: merge` y `env_vars_update_strategy: merge` se conserva lo que ya tiene el servicio. Hoy está conectado así:

    env_vars: |-
      CONFIG_SERVER_URL=<direccion del servidor de parametros>
      DB_URL=jdbc:postgresql://<host de Neon>/smartlife?sslmode=require
      DB_USER=smartlife_app
    env_vars_update_strategy: merge
    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
    secrets_update_strategy: merge

El despliegue solo corre con un push a `main`.

## Qué hacer cuando llega una clave

**Paso 1.** En Secret Manager, abrir el secreto (por ejemplo `strapi-api-token`), pestaña **Versiones**, **Versión nueva**, pegar la clave y guardar. La clave se escribe **solo ahí**.

**Paso 2.** Agregar su línea al bloque `secrets` del paso `Deploy`. Por ejemplo, para Strapi quedaría:

    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
      STRAPI_API_TOKEN=strapi-api-token:latest
    secrets_update_strategy: merge

**Paso 3.** Agregar como variables normales las que acompañan a esa clave (por ejemplo `STRAPI_URL`).

**Regla para no romper el despliegue:** una línea solo se agrega cuando el secreto **ya tiene al menos una versión**. Si apunta a un secreto vacío, el despliegue de Cloud Run falla.

Si el despliegue falla por permisos sobre un secreto, la cuenta de servicio de la variable `GCP_SERVICE_ACCOUNT` puede necesitar acceso de lectura a ese secreto.

## Costos

Se usa una sola versión activa y una sola ubicación por secreto. Los secretos vacíos no tienen versiones. Los límites gratuitos de Secret Manager (versiones activas y operaciones de acceso al mes) se confirman en https://cloud.google.com/secret-manager/pricing, porque Google puede cambiarlos. La base de datos está en el plan gratuito de Neon (sus límites se confirman en https://neon.com/pricing). No se debe pulsar "Actualizar" ni "Upgrade" en la cuenta de prueba de Google Cloud ni en Neon sin avisar al equipo.

## Pendiente

- Probar la conexión a Neon desde Cloud Run: depende de terminar la lógica de `AuthorizeVisitUseCaseImpl` y de subir los cambios a `main`.
- Cargar datos de prueba en Neon y refrescar las vistas materializadas.
- Cambiar la contraseña de `smartlife_app` en Neon (y agregar la nueva como versión de `db-password`) antes de cargar datos reales.
- Cuando existan las cuentas de Strapi, Cloudflare R2 y Redis, agregar las claves y sus líneas en el `cicd.yml`.