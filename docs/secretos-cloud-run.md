# Secretos y variables de Cloud Run

Este documento explica cómo se guardan las claves de SmartLife en **Google Cloud Secret Manager** y cómo llegan a Cloud Run. Las claves **nunca** se escriben en archivos del repositorio, en el chat ni en GitHub.

- Proyecto de Google Cloud: `smartlife-application` (ID `project-078c9c72-e891-42db-98c`)
- Servicio de Cloud Run: `smartlife-application`, región `us-central1`
- Cuenta de servicio que lee los secretos: `smartlife-runtime@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com`
- Rol dado a esa cuenta, solo sobre cada secreto: **Usuario con acceso a secretos de Secret Manager** (`roles/secretmanager.secretAccessor`)

## Secretos (van a Secret Manager)

Cada secreto está en una sola ubicación (`us-central1`) y con cifrado administrado por Google, para mantenerse dentro de lo gratuito.

| Variable que lee la aplicación | Secreto en Secret Manager | Estado |
|---|---|---|
| `RESEND_API_KEY` | `resend-api-key` | Con valor (versión 1) y conectado en el despliegue |
| `STRAPI_API_TOKEN` | `strapi-api-token` | Creado, vacío: falta el token de Strapi |
| `R2_ACCESS_KEY` | `r2-access-key` | Creado, vacío: falta la cuenta de Cloudflare R2 |
| `R2_SECRET_KEY` | `r2-secret-key` | Creado, vacío: falta la cuenta de Cloudflare R2 |
| `REDIS_PASSWORD` | `redis-password` | Creado, vacío: falta el servidor de Redis |
| `DB_PASSWORD` | Pendiente | Revisar cómo está guardada hoy en Cloud Run y, si es una variable normal, moverla a un secreto |
| `FIREBASE_CREDENTIALS_BASE64` | No se usa | El adaptador usa las credenciales de la cuenta de servicio de Cloud Run |

## Variables normales (no son secretas)

Van como variables de entorno del servicio de Cloud Run. Valores por defecto tomados de `application.properties`:

| Variable | Valor por defecto |
|---|---|
| `DB_URL`, `DB_USER` | Sin valor por defecto (obligatorias) |
| `DDL_AUTO` | `none` |
| `CONFIG_SERVER_URL` | `http://localhost:8888` |
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

El paso `Deploy` de `.github/workflows/cicd.yml` usa la acción `google-github-actions/deploy-cloudrun`. Los secretos se listan en su opción `secrets`, con el formato `VARIABLE=nombre-del-secreto:versión`. Con `secrets_update_strategy: merge` se conservan los secretos que ya tiene el servicio. Hoy está conectado así:

    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
    secrets_update_strategy: merge

El despliegue solo corre con un push a `main`.

## Qué hacer cuando llega una clave

**Paso 1.** En Secret Manager, abrir el secreto (por ejemplo `strapi-api-token`), pestaña **Versiones**, **Versión nueva**, pegar la clave y guardar. La clave se escribe **solo ahí**.

**Paso 2.** Agregar su línea al bloque `secrets` del paso `Deploy`. Por ejemplo, para Strapi quedaría:

    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      STRAPI_API_TOKEN=strapi-api-token:latest
    secrets_update_strategy: merge

**Paso 3.** Agregar como variables normales las que acompañan a esa clave (por ejemplo `STRAPI_URL`).

**Regla para no romper el despliegue:** una línea solo se agrega cuando el secreto **ya tiene al menos una versión**. Si apunta a un secreto vacío, el despliegue de Cloud Run falla.

Si el despliegue falla por permisos sobre un secreto, la cuenta de servicio de la variable `GCP_SERVICE_ACCOUNT` puede necesitar acceso de lectura a ese secreto.

## Costos

Se usa una sola versión activa y una sola ubicación por secreto. Los secretos vacíos no tienen versiones. Los límites gratuitos de Secret Manager (versiones activas y operaciones de acceso al mes) se confirman en https://cloud.google.com/secret-manager/pricing, porque Google puede cambiarlos. No se debe actualizar la cuenta de prueba a una de pago sin avisar al equipo.

## Pendiente

- Mover `DB_PASSWORD` a un secreto, con aprobación de quien administra el servicio.
- Cuando existan las cuentas de Strapi, Cloudflare R2 y Redis, agregar las claves y sus líneas en el `cicd.yml`.