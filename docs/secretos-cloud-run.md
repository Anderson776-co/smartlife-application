# Secretos, variables y servicios externos de Cloud Run

Este documento explica cómo se guardan las claves de SmartLife en **Google Cloud Secret Manager**, cómo llegan a Cloud Run, dónde vive la base de datos y cómo quedaron conectados los servicios externos. Las claves **nunca** se escriben en archivos del repositorio, en el chat ni en GitHub.

- Proyecto de Google Cloud: `smartlife-application` (ID `project-078c9c72-e891-42db-98c`)
- Servicio de Cloud Run: `smartlife-application`, región `us-central1`
- Cuenta de servicio que lee los secretos: `smartlife-runtime@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com`
- Rol dado a esa cuenta, solo sobre cada secreto: **Usuario con acceso a secretos de Secret Manager** (`roles/secretmanager.secretAccessor`)

## Secretos (van a Secret Manager)

Cada secreto está en una sola ubicación (`us-central1`) y con cifrado administrado por Google, para mantenerse dentro de lo gratuito.

| Variable que lee la aplicación | Secreto en Secret Manager | Estado |
|---|---|---|
| `RESEND_API_KEY` | `resend-api-key` | Con valor (versión 1) y conectado en el despliegue |
| `DB_PASSWORD` | `db-password` | Con valor y conectado. La **versión 3** es la contraseña del rol `smartlife_app` de Neon y es la que se lee (`latest`). Las versiones 1 y 2 eran de una base anterior en Cloud SQL, ya eliminada: no se usan y se pueden inhabilitar |
| `R2_ACCESS_KEY` | `r2-access-key` | Con valor (versión 1) y conectado en el despliegue |
| `R2_SECRET_KEY` | `r2-secret-key` | Con valor (versión 1) y conectado en el despliegue |
| `STRAPI_API_TOKEN` | `strapi-api-token` | Creado, vacío: falta el token de Strapi |
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

## Firebase (inicio de sesión)

- Proyecto de Firebase: `pocfirebase-c2915`.
- La aplicación **no usa ningún archivo de claves**: toma las credenciales de la cuenta de servicio de Cloud Run (`smartlife-runtime`), a la que se le dio el rol **Firebase Authentication Admin** (`roles/firebaseauth.admin`) dentro del proyecto de Firebase.
- La API `identitytoolkit.googleapis.com` está activada en el proyecto de Google Cloud de la aplicación.
- La variable `FIREBASE_PROJECT_ID` se define en `.github/workflows/cicd.yml`.
- Nunca se debe guardar un archivo de claves de Firebase (por ejemplo `firebase-service-account.json`) en un repositorio.
- Falta probar un inicio de sesión real desde Cloud Run.

## Cloudflare R2 (documentos de las visitas)

- Bucket: `smartlife-documentos`, **privado** (sin dominio personalizado y con la dirección pública `r2.dev` desactivada), clase de almacenamiento **Standard**.
- Acceso: un token de cuenta con permiso **Object Read & Write**, limitado solo a ese bucket. Sus dos claves están en los secretos `r2-access-key` y `r2-secret-key`.
- `R2_ENDPOINT` es la dirección S3 de la cuenta (`https://<ID-de-cuenta>.r2.cloudflarestorage.com`, sin el nombre del bucket) y `R2_BUCKET` es el nombre del bucket. Ambos van en el `cicd.yml`.
- Costo: la suscripción está activa y hay una alerta de presupuesto en Cloudflare. Los límites gratuitos mensuales que mostró la pantalla de R2 son 10 GB de almacenamiento, 1 millón de operaciones de escritura y 10 millones de lectura; la descarga no se cobra. Solo se cobra si se superan.
- Probado el 29 de octubre de 2026 con un archivo de prueba: se subió, se leyó con las claves y se borró; una petición sin claves no recibió el archivo. Falta probarlo desde la aplicación.

## WAF de Cloudflare (api.smartlife.sarl)

El dominio `smartlife.sarl` está en Cloudflare (plan gratuito). La API se publica en un subdominio aparte, `api.smartlife.sarl`, y el camino de una petición es:

    Visitante -> Cloudflare (WAF) -> Worker smartlife-api-proxy -> Cloud Run (smartlife-application)

- **Worker `smartlife-api-proxy`:** reenvía cada petición, tal cual, a la dirección de Cloud Run. Se usa un Worker porque Cloud Run rechaza las peticiones que llegan con un nombre de dominio distinto al suyo. Su código es este:

        const ORIGEN = "smartlife-application-clfarpgyxa-uc.a.run.app";

        export default {
          async fetch(request) {
            const url = new URL(request.url);
            url.protocol = "https:";
            url.hostname = ORIGEN;
            url.port = "";
            return fetch(new Request(url.toString(), request), { redirect: "manual" });
          },
        };

- **Dominio del Worker:** `api.smartlife.sarl` (dominio personalizado). La dirección `workers.dev` del Worker está **desactivada**, para que no exista una entrada que se salte el WAF.
- **Reglas administradas gratuitas de Cloudflare:** activadas.
- **Reglas personalizadas** (todas llevan la condición `http.host eq "api.smartlife.sarl"` para no afectar a `smartlife.sarl` ni a `www`, que ya están publicadas). Acción: bloquear.
  1. `api-bloquear-actuator-y-documentacion`: rutas que empiezan por `/actuator`, `/v3/api-docs` o `/swagger-ui`.
  2. `api-bloquear-metodos-no-usados`: cualquier método HTTP distinto de GET, POST, PUT, PATCH, DELETE, OPTIONS y HEAD.
  3. `api-bloquear-sondeos-comunes`: rutas que empiezan por `/.env`, `/.git`, `/wp-` o `/phpmyadmin`, y rutas que terminan en `.php`.
- **Límite de peticiones:** la regla `Limit` (ya existía; el plan gratuito permite una sola) se aplica a todas las rutas del dominio: 30 peticiones cada 10 segundos por IP, acción bloquear durante 10 segundos. Cubre también la API. Varias personas en la misma red cuentan como una sola IP.
- Probado el 29 de octubre de 2026 con `curl`: las rutas bloqueadas responden con la página «Attention Required» de Cloudflare, mientras que una ruta normal llega a Cloud Run.
- **Bot Fight Mode:** se dejó apagado, porque en el plan gratuito aplica a todo el dominio y puede mostrar retos que el frontend no sabe resolver.

Limitaciones conocidas:

- **El servicio de Cloud Run es privado** (no tiene el rol `roles/run.invoker` para `allUsers`), así que hoy responde `403` de Google a cualquier petición, también por el WAF. Para que la API reciba peticiones del frontend hay que hacerlo público, decisión que corresponde a Anderson.
- Aun público, quien llame directo a `smartlife-application-clfarpgyxa-uc.a.run.app` se salta el WAF. Cerrarlo sin costo exige que el Worker envíe una contraseña en un encabezado y que la aplicación la exija: queda como mejora.
- El plan gratuito no incluye el conjunto de reglas OWASP completo.

## Variables normales (no son secretas)

Van como variables de entorno del servicio de Cloud Run. Las primeras seis se definen en `.github/workflows/cicd.yml`; el resto usa los valores por defecto de `application.properties`:

| Variable | Valor |
|---|---|
| `DB_URL` | Dirección de Neon (ver arriba) |
| `DB_USER` | `smartlife_app` |
| `CONFIG_SERVER_URL` | Dirección del servidor de parámetros en Cloud Run (por defecto `http://localhost:8888`) |
| `FIREBASE_PROJECT_ID` | `pocfirebase-c2915` |
| `R2_ENDPOINT` | Dirección S3 de la cuenta de Cloudflare |
| `R2_BUCKET` | `smartlife-documentos` |
| `DDL_AUTO` | `none` |
| `STRAPI_URL` | vacío |
| `RESEND_FROM` | `onboarding@resend.dev` |
| `CACHE_TYPE` | `none` (para activar Redis: `redis`) |
| `CACHE_TTL` | `1h` |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_SSL` | `localhost`, `6379`, vacío, `false` |
| `SESSION_DURATION`, `SESSION_COOKIE_NAME`, `SESSION_CHECK_REVOKED` | `5d`, `__Host-smartlife_session`, `true` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` (hay que cambiarlo por la dirección del frontend publicado) |
| `QR_BASE_URL` | `http://localhost:5173/visitas/autorizaciones/` |

## Cómo se conecta un secreto

El paso `Deploy` de `.github/workflows/cicd.yml` usa la acción `google-github-actions/deploy-cloudrun`. Los secretos se listan en su opción `secrets`, con el formato `VARIABLE=nombre-del-secreto:versión`, y las variables normales en `env_vars`. Con `secrets_update_strategy: merge` y `env_vars_update_strategy: merge` se conserva lo que ya tiene el servicio. Hoy está conectado así:

    env_vars: |-
      CONFIG_SERVER_URL=<direccion del servidor de parametros>
      DB_URL=jdbc:postgresql://<host de Neon>/smartlife?sslmode=require
      DB_USER=smartlife_app
      FIREBASE_PROJECT_ID=pocfirebase-c2915
      R2_ENDPOINT=<direccion S3 de la cuenta de Cloudflare>
      R2_BUCKET=smartlife-documentos
    env_vars_update_strategy: merge
    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
      R2_ACCESS_KEY=r2-access-key:latest
      R2_SECRET_KEY=r2-secret-key:latest
    secrets_update_strategy: merge

El despliegue solo corre con un push a `main`.

## Qué hacer cuando llega una clave

**Paso 1.** En Secret Manager, abrir el secreto (por ejemplo `strapi-api-token`), pestaña **Versiones**, **Versión nueva**, pegar la clave y guardar. La clave se escribe **solo ahí**.

**Paso 2.** Agregar su línea al bloque `secrets` del paso `Deploy`. Por ejemplo, para Strapi quedaría:

    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
      R2_ACCESS_KEY=r2-access-key:latest
      R2_SECRET_KEY=r2-secret-key:latest
      STRAPI_API_TOKEN=strapi-api-token:latest
    secrets_update_strategy: merge

**Paso 3.** Agregar como variables normales las que acompañan a esa clave (por ejemplo `STRAPI_URL`).

**Regla para no romper el despliegue:** una línea solo se agrega cuando el secreto **ya tiene al menos una versión**. Si apunta a un secreto vacío, el despliegue de Cloud Run falla.

Si el despliegue falla por permisos sobre un secreto, la cuenta de servicio de la variable `GCP_SERVICE_ACCOUNT` puede necesitar acceso de lectura a ese secreto.

## Costos

Se usa una sola versión activa y una sola ubicación por secreto. Los secretos vacíos no tienen versiones. Los límites gratuitos de Secret Manager se confirman en https://cloud.google.com/secret-manager/pricing, porque Google puede cambiarlos. La base de datos está en el plan gratuito de Neon (https://neon.com/pricing), los documentos en R2 dentro de su capa gratuita (https://developers.cloudflare.com/r2/pricing/) y el WAF en el plan gratuito de Cloudflare. No se debe pulsar «Actualizar» ni «Upgrade» en las cuentas de prueba de Google Cloud, Neon o Cloudflare sin avisar al equipo.

## Pendiente

- Probar Neon, Firebase y R2 desde Cloud Run: depende de terminar la lógica de `AuthorizeVisitUseCaseImpl` y de subir los cambios a `main`.
- Decidir si el servicio de Cloud Run se hace público, para que el frontend pueda usar `api.smartlife.sarl`, y ajustar `CORS_ALLOWED_ORIGINS` a la dirección del frontend publicado.
- Cargar datos de prueba en Neon y refrescar las vistas materializadas.
- Cambiar la contraseña de `smartlife_app` en Neon (y agregar la nueva como versión de `db-password`) antes de cargar datos reales.
- Cuando existan las cuentas de Strapi y Redis, agregar las claves y sus líneas en el `cicd.yml`.
- Verificar un dominio en Resend para poder enviar correos a cualquier visitante.