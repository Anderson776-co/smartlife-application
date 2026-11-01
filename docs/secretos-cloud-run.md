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
| `RESEND_API_KEY` | `resend-api-key` | Con valor (versión 1), de la cuenta de Resend de Mateo, y conectado en el despliegue |
| `DB_PASSWORD` | `db-password` | Con valor y conectado. La **versión 3** es la contraseña del rol `smartlife_app` de Neon y es la que se lee (`latest`). Las versiones 1 y 2 eran de una base anterior en Cloud SQL, ya eliminada: no se usan y se pueden inhabilitar |
| `R2_ACCESS_KEY` | `r2-access-key` | Con valor (versión 1) y conectado en el despliegue |
| `R2_SECRET_KEY` | `r2-secret-key` | Con valor (versión 1) y conectado en el despliegue |
| `STRAPI_API_TOKEN` | `strapi-api-token` | Creado, vacío: falta el token de Strapi |
| `REDIS_PASSWORD` | `redis-password` | Con valor (versión 1): el token de la base de Upstash, y conectado en el despliegue |
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
- Datos de prueba cargados el 30 de octubre de 2026: una urbanización, dos usuarios (`carlos@example.com`, propietario de la casa 101 del bloque A y del apartamento 202 del bloque B, y `maria@example.com`, residente del 202 del bloque B), los motivos de visita y una autorización de ejemplo. Los `external_id` de esos dos usuarios son los UID reales de sus cuentas de Firebase. El `05-datosprueba.sql` del repositorio trae UID inventados: para Neon hay que usar los reales.
- **Los usuarios nuevos se crean a mano en Neon** (decisión del equipo; el registro de usuarios es un caso de uso aparte). El registro del frontend solo crea la cuenta en Firebase, y el login busca al usuario en Neon por su UID de Firebase (`users.user.external_id`). Para que alguien pueda entrar hay que sacar su UID de Firebase, crear su fila en `users.user` y su rol en `users.user_role`, y refrescar las vistas materializadas (`dwellings.user_owner`, `residents.user_resident` y `visits.authorizator`). Para autorizar visitas, además debe ser propietario de una vivienda o residente activo de ella.

## Firebase (inicio de sesión)

- Proyecto de Firebase: `pocfirebase-c2915`, **confirmado por el equipo como el definitivo**. Su nombre visible es «Smartlife-firebaseComponent»; el ID conserva `poc` porque nació como prueba de concepto y no se puede cambiar.
- La aplicación **no usa ningún archivo de claves**: toma las credenciales de la cuenta de servicio de Cloud Run (`smartlife-runtime`), a la que se le dio el rol **Firebase Authentication Admin** (`roles/firebaseauth.admin`) dentro del proyecto de Firebase.
- La API `identitytoolkit.googleapis.com` está activada en el proyecto de Google Cloud de la aplicación.
- La variable `FIREBASE_PROJECT_ID` se define en `.github/workflows/cicd.yml`.
- Nunca se debe guardar un archivo de claves de Firebase (por ejemplo `firebase-service-account.json`) en un repositorio.
- El inicio de sesión del frontend usa correo y contraseña (activado en el proyecto); el acceso anónimo está apagado. Los dominios autorizados son los de por defecto, y no hace falta agregar `smartlife.sarl` mientras el frontend no pida enlaces de retorno en los correos de verificación.
- Falta probar un inicio de sesión real desde Cloud Run.

## Frontend (Firebase Hosting)

Según el ADR-0022, el frontend (Angular) se aloja en **Firebase Hosting**, dentro del mismo proyecto de Firebase (`pocfirebase-c2915`, plan Spark, sin costo). Los registros DNS de `smartlife.sarl` y `www` apuntaban a un nombre de Vercel que no tenía ningún despliegue: es un resto y no la decisión del equipo.

- Configuración en la carpeta `Frontend`: `firebase.json` (publica `dist/smartlife-frontend/browser` y reescribe todas las rutas hacia `index.html`) y `.firebaserc` (proyecto `pocfirebase-c2915`).
- Publicación manual, hecha el 31 de octubre de 2026 desde Cloud Shell con la cuenta de Anderson: clonar la rama, `npm ci`, `npm run build` y `npx firebase-tools@latest deploy --only hosting --project pocfirebase-c2915`. Para que la herramienta de Firebase funcione desde Cloud Shell hay que activar `firebase.googleapis.com` y `firebasehosting.googleapis.com` en el proyecto de Google Cloud de la aplicación.
- Dirección gratuita: `https://pocfirebase-c2915.web.app`. Ahí la API **no responde**, porque esa dirección no pasa por Cloudflare.
- Dominio `smartlife.sarl`: **conectado el 31 de octubre de 2026** como dominio personalizado de Firebase Hosting. En Cloudflare se reemplazó el registro CNAME hacia Vercel por un registro `A` (`199.36.158.100`) y un `TXT` (`hosting-site=pocfirebase-c2915`). Mientras Firebase verificaba el dominio y emitía el certificado, el registro `A` estuvo sin proxy («DNS only»); ya verificado, se pasó a «Proxied» para que funcione la ruta `/api/*` del Worker, con el modo de cifrado Full. Firebase puede mostrar que el DNS «no coincide» porque ve las direcciones de Cloudflare y no las suyas; mientras el certificado esté emitido, la página se sirve (es el costo de tener Firebase detrás de Cloudflare, previsto en el ADR-0022). Comprobado con `curl`: `/` y `/login` responden `200`.
- Registro `www`: es un CNAME hacia `smartlife.sarl` (con proxy) y una regla de redirección de Cloudflare, `www-a-dominio-principal` (`301`, conserva la ruta y los parámetros), lo manda al dominio principal. Por eso no hace falta una ruta del Worker para `www`.
- El frontend llama a la API como `/api/v1` (ruta relativa). En desarrollo lo atiende el proxy de `ng serve`; en producción lo atiende la ruta de Cloudflare `smartlife.sarl/api/*`.
- El frontend no tiene página de destino para el enlace del QR (`/visitas/autorizaciones/<id>`): hoy esa ruta lleva al login.
- Si algo fallara el día de la demostración, el frontend puede correr en un computador (`ng serve`), que redirige `/api` al backend local.

## Cloudflare R2 (documentos de las visitas)

- Bucket: `smartlife-documentos`, **privado** (sin dominio personalizado y con la dirección pública `r2.dev` desactivada), clase de almacenamiento **Standard**.
- Acceso: un token de cuenta con permiso **Object Read & Write**, limitado solo a ese bucket. Sus dos claves están en los secretos `r2-access-key` y `r2-secret-key`.
- `R2_ENDPOINT` es la dirección S3 de la cuenta (`https://<ID-de-cuenta>.r2.cloudflarestorage.com`, sin el nombre del bucket) y `R2_BUCKET` es el nombre del bucket. Ambos van en el `cicd.yml`.
- Costo: la suscripción está activa y hay una alerta de presupuesto en Cloudflare. Los límites gratuitos mensuales que mostró la pantalla de R2 son 10 GB de almacenamiento, 1 millón de operaciones de escritura y 10 millones de lectura; la descarga no se cobra. Solo se cobra si se superan.
- Probado el 29 de octubre de 2026 con un archivo de prueba: se subió, se leyó con las claves y se borró; una petición sin claves no recibió el archivo. Falta probarlo desde la aplicación.

## Resend (correos)

- La cuenta de Resend es la de Mateo, en el plan gratuito (los límites diarios y mensuales se confirman en https://resend.com/pricing). La clave de API está en el secreto `resend-api-key`.
- Dominio de envío: `mail.smartlife.sarl`, un subdominio de `smartlife.sarl` solo para correo, para que sus registros no se mezclen con los de la página. Región `us-east-1` (North Virginia). **Verificado.** Solo envío: la recepción está apagada, y el seguimiento de clics y de aperturas también (para no alterar el enlace del QR).
- Remitente: `no-reply@mail.smartlife.sarl`, que se define con la variable `RESEND_FROM` en `.github/workflows/cicd.yml`. Si no se define, la aplicación usa `onboarding@resend.dev`, que solo sirve para pruebas.
- Registros DNS creados en Cloudflare (todos en modo «DNS only», sin proxy):
  - `resend._domainkey.mail` (TXT): la firma del correo (DKIM).
  - `send.mail` y `rsend.mail` (CNAME): autorización de envío y recepción de rebotes.
  - `_dmarc` (TXT): política `v=DMARC1; p=none;`, que solo observa y no bloquea.
- Los registros los agregó Mateo a mano en Cloudflare. No se usó la opción «Auto configure» de Resend, para no darle a un servicio externo permiso sobre el DNS del dominio.
- Probado el 29 de octubre de 2026: se envió un correo de prueba desde Cloud Shell con la clave del baúl de secretos, desde `no-reply@mail.smartlife.sarl` a una dirección distinta a la de la cuenta de Resend, y llegó. Falta probar el envío desde la aplicación, que además necesita la plantilla del correo en Strapi.
- Si cambia la persona responsable de la cuenta de Resend, hay que crear una clave en la cuenta nueva, verificar allí el dominio y guardar la clave como una versión nueva de `resend-api-key`.

## Redis (caché de los textos de Strapi)

Según el ADR-0036, la caché es Redis. Guarda los mensajes y las plantillas de correo que vienen de Strapi, para no consultarlo en cada petición.

- Servicio: **Upstash**, plan **Free** (sin costo; la pantalla de la base muestra 500 mil comandos al mes y 250 MB), base `smartlife-cache`, en Google Cloud `us-central1` (Iowa), la misma región que Cloud Run. La conexión va cifrada (TLS) y con contraseña.
- Datos de conexión (no son secretos): host `rapid-sculpin-44456.upstash.io`, puerto `6379`, usuario `default`, cifrado activado. Van en el `cicd.yml` como `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME` y `REDIS_SSL=true`, junto con `CACHE_TYPE=redis`.
- La contraseña es el **Token** normal de la base (no el *Readonly Token*: la aplicación necesita escribir) y vive solo en el secreto `redis-password` (versión 1), que llega a la aplicación como `REDIS_PASSWORD`.
- Comportamiento: los datos duran 1 hora (`CACHE_TTL`) y las claves llevan el prefijo `smartlife::`. Solo se guardan respuestas correctas de Strapi: si Strapi falla o no encuentra el texto, la aplicación lanza una excepción y Spring no la guarda. Si Redis se cae, un manejador (`LoggingCacheErrorHandler`) solo registra el error y la aplicación sigue consultando a Strapi. La revisión de salud de Redis está apagada para que una caída de Redis no marque la aplicación como enferma. Los objetos que se guardan (`MessageCatalogDTO` y `NotificationCatalogDTO`) implementan `Serializable`.
- No se guardan datos personales ni credenciales: solo textos de mensajes y de plantillas.
- Probado el 31 de octubre de 2026 desde Cloud Shell, con la contraseña leída del baúl de secretos: `PING`, guardar un dato con vencimiento de 60 segundos, leerlo y borrarlo. Falta probarlo desde la aplicación desplegada.
- Si cambia la persona que administra la cuenta de Upstash, hay que crear la base o el token en la cuenta nueva y guardar el token como una versión nueva de `redis-password` (se lee con `:latest`, así que no hay que tocar el `cicd.yml`).
- La alternativa sin servidor es `CACHE_TYPE=simple`, una caché en la memoria de la aplicación: sin vencimiento, una por instancia y se pierde al reiniciar. El ADR-0036 la descartó, pero sirve de respaldo.

## Camino de las peticiones (Cloudflare + API Gateway + Cloud Run)

La API se publica en `api.smartlife.sarl` y también en la ruta `smartlife.sarl/api/*`, ambos dentro del dominio `smartlife.sarl` (en Cloudflare, plan gratuito). El camino de una petición a la API es:

    Visitante -> Cloudflare (WAF) -> Worker smartlife-api-proxy -> API Gateway (Google) -> Cloud Run (privado)

La página (el resto de rutas de `smartlife.sarl`) la sirve Firebase Hosting. El único camino al backend es el de arriba: Cloud Run no acepta peticiones sin identidad, el API Gateway exige una clave que solo conoce el Worker, y la dirección `workers.dev` del Worker está desactivada.

## WAF de Cloudflare (api.smartlife.sarl)

- **Reglas administradas gratuitas de Cloudflare:** activadas.
- **Reglas personalizadas** (todas llevan la condición `http.host eq "api.smartlife.sarl"` para no afectar a `smartlife.sarl` ni a `www`, que ya están publicadas). Acción: bloquear.
  1. `api-bloquear-actuator-y-documentacion`: rutas que empiezan por `/actuator`, `/v3/api-docs` o `/swagger-ui`.
  2. `api-bloquear-metodos-no-usados`: cualquier método HTTP distinto de GET, POST, PUT, PATCH, DELETE, OPTIONS y HEAD, en `api.smartlife.sarl` y en las rutas `/api/` de `smartlife.sarl` y `www.smartlife.sarl`.
  3. `api-bloquear-sondeos-comunes`: rutas que empiezan por `/.env`, `/.git`, `/wp-` o `/phpmyadmin`, y rutas que terminan en `.php`.
- **Límite de peticiones:** la regla `Limit` (ya existía; el plan gratuito permite una sola) se aplica a todas las rutas del dominio: 30 peticiones cada 10 segundos por IP, acción bloquear durante 10 segundos. Cubre también la API. Varias personas en la misma red cuentan como una sola IP.
- **Bot Fight Mode:** se dejó apagado, porque en el plan gratuito aplica a todo el dominio y puede mostrar retos que el frontend no sabe resolver.
- El plan gratuito no incluye el conjunto de reglas OWASP completo.

## Worker de Cloudflare (smartlife-api-proxy)

Reenvía cada petición, tal cual, al API Gateway y le agrega la clave de API. La clave vive solo como secreto del Worker (`GATEWAY_API_KEY`, en Settings > Variables and secrets); nunca se escribe en un archivo ni en el repositorio. Su código es este:

        const ORIGEN = "smartlife-gateway-29p679a1.uc.gateway.dev";

        export default {
          async fetch(request, env) {
            const url = new URL(request.url);
            url.protocol = "https:";
            url.hostname = ORIGEN;
            url.port = "";
            const reenviada = new Request(url.toString(), request);
            reenviada.headers.set("x-api-key", env.GATEWAY_API_KEY);
            return fetch(reenviada, { redirect: "manual" });
          },
        };

- Dominio del Worker: `api.smartlife.sarl` (dominio personalizado), y la ruta `smartlife.sarl/api/*` (con el modo de fallo «Fail closed»). La dirección `workers.dev` está **desactivada**. `www.smartlife.sarl` no necesita ruta propia porque redirige al dominio principal. Cuidado al crear una ruta: la ventana de Cloudflare propone por defecto `*.smartlife.sarl/*`, que enviaría toda la página al Worker.

## API Gateway (Google Cloud)

- API `smartlife-api`, configuración `smartlife-config-1` y gateway `smartlife-gateway` en `us-central1`. Dirección: `smartlife-gateway-29p679a1.uc.gateway.dev`.
- El contrato (Swagger 2.0) está en `infra/api-gateway/openapi2.yaml`. Solo define las rutas que expone la aplicación; cualquier otra ruta recibe `404` del gateway:
  - `GET /api/v1/users/sessions/csrf`
  - `POST /api/v1/users/sessions`
  - `GET /api/v1/users/sessions/me`
  - `POST /api/v1/users/sessions/logout`
  - `POST /api/v1/visits/authorizations`
  - y la verificación previa de CORS (`OPTIONS`) de cada una. El CORS lo resuelve la propia aplicación, no el gateway.
- Plazo máximo de respuesta del gateway: 60 segundos (la primera petición a Cloud Run "frío" puede tardar).
- Cuenta de servicio del gateway: `smartlife-gateway@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com`, con el rol `roles/run.invoker` **solo sobre el servicio** `smartlife-application`. El servicio de Cloud Run sigue siendo privado (no tiene `allUsers`).
- Clave de API: `smartlife-worker-cloudflare`, limitada solo al servicio del gateway. Sin esa clave, el gateway responde `401`.
- Probado el 29 de octubre de 2026 con `curl`:
  - `api.smartlife.sarl`: llega a la aplicación (la versión desplegada hoy viene de `main` y no tiene todavía las rutas nuevas, por eso responde `404` de Spring).
  - Gateway sin clave: `401`. `run.app` directo: `403`. `workers.dev`: `404`.
  - `/actuator/health` por `api.smartlife.sarl`: lo bloquea el WAF.
- **Si se agrega una ruta a la aplicación**, hay que agregarla al contrato y crear una configuración nueva:

        gcloud api-gateway api-configs create smartlife-config-2 --api=smartlife-api --openapi-spec=infra/api-gateway/openapi2.yaml --backend-auth-service-account=smartlife-gateway@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com --project=project-078c9c72-e891-42db-98c
        gcloud api-gateway gateways update smartlife-gateway --api=smartlife-api --api-config=smartlife-config-2 --location=us-central1 --project=project-078c9c72-e891-42db-98c

## Variables normales (no son secretas)

Van como variables de entorno del servicio de Cloud Run. Las que aparecen en el bloque `env_vars` de `.github/workflows/cicd.yml` (ver más abajo) se definen en el despliegue; el resto usa los valores por defecto de `application.properties`:

| Variable | Valor |
|---|---|
| `DB_URL` | Dirección de Neon (ver arriba) |
| `DB_USER` | `smartlife_app` |
| `CONFIG_SERVER_URL` | Dirección del servidor de parámetros en Cloud Run (por defecto `http://localhost:8888`) |
| `FIREBASE_PROJECT_ID` | `pocfirebase-c2915` |
| `R2_ENDPOINT` | Dirección S3 de la cuenta de Cloudflare |
| `R2_BUCKET` | `smartlife-documentos` |
| `RESEND_FROM` | `no-reply@mail.smartlife.sarl` |
| `CORS_ALLOWED_ORIGINS` | `https://smartlife.sarl` (hace falta aunque el frontend use la ruta de Cloudflare: el backend ve el `Origin` del navegador y lo trata como otro sitio; una sola dirección, porque una coma dentro de la variable puede interpretarse como separador en el despliegue) |
| `DDL_AUTO` | `none` |
| `STRAPI_URL` | vacío (Strapi está a cargo de Anderson) |
| `CACHE_TYPE` | `redis` (definida en el `cicd.yml`; `none` apaga la caché y `simple` usa una caché en memoria, sin vencimiento) |
| `CACHE_TTL` | `1h` |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_SSL` | `rapid-sculpin-44456.upstash.io`, `6379`, `default`, `true` (definidas en el `cicd.yml`) |
| `OTLP_METRICS_ENABLED`, `OTLP_TRACING_ENABLED` | `false`: la telemetría está apagada (ver Pendiente) |
| `SESSION_DURATION`, `SESSION_COOKIE_NAME`, `SESSION_CHECK_REVOKED` | `5d`, `__Host-smartlife_session`, `true` |
| `QR_BASE_URL` | `http://localhost:5173/visitas/autorizaciones/` (valor de respaldo; el servidor de parámetros entrega `https://smartlife.sarl/visitas/autorizaciones/`, que tiene prioridad) |

## Cómo se conecta un secreto

El paso `Deploy` de `.github/workflows/cicd.yml` usa la acción `google-github-actions/deploy-cloudrun`. Los secretos se listan en su opción `secrets`, con el formato `VARIABLE=nombre-del-secreto:versión`, y las variables normales en `env_vars`. Con `secrets_update_strategy: merge` y `env_vars_update_strategy: merge` se conserva lo que ya tiene el servicio. Hoy está conectado así:

    env_vars: |-
      CONFIG_SERVER_URL=<direccion del servidor de parametros>
      DB_URL=jdbc:postgresql://<host de Neon>/smartlife?sslmode=require
      DB_USER=smartlife_app
      FIREBASE_PROJECT_ID=pocfirebase-c2915
      R2_ENDPOINT=<direccion S3 de la cuenta de Cloudflare>
      R2_BUCKET=smartlife-documentos
      RESEND_FROM=no-reply@mail.smartlife.sarl
      CORS_ALLOWED_ORIGINS=https://smartlife.sarl
      CACHE_TYPE=redis
      REDIS_HOST=<host de Upstash>
      REDIS_PORT=6379
      REDIS_USERNAME=default
      REDIS_SSL=true
    env_vars_update_strategy: merge
    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
      R2_ACCESS_KEY=r2-access-key:latest
      R2_SECRET_KEY=r2-secret-key:latest
      REDIS_PASSWORD=redis-password:latest
    secrets_update_strategy: merge

El despliegue solo corre con un push a `main`. El servicio se despliega con `--memory=1Gi --max-instances=1 --cpu-boost` (más memoria que la de por defecto, una sola instancia para quedar dentro de lo gratuito y más CPU durante el arranque de Spring Boot).

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

Se usa una sola versión activa y una sola ubicación por secreto. Los secretos vacíos no tienen versiones. Los límites gratuitos de Secret Manager se confirman en https://cloud.google.com/secret-manager/pricing, porque Google puede cambiarlos. La base de datos está en el plan gratuito de Neon (https://neon.com/pricing), los documentos en R2 dentro de su capa gratuita (https://developers.cloudflare.com/r2/pricing/), los correos en el plan gratuito de Resend (https://resend.com/pricing), la caché en el plan gratuito de Upstash (https://upstash.com/pricing), el WAF y el Worker en el plan gratuito de Cloudflare, y el API Gateway dentro de su capa gratuita mensual (https://cloud.google.com/api-gateway/pricing). No se debe pulsar «Actualizar» ni «Upgrade» en las cuentas de prueba de Google Cloud, Neon, Cloudflare o Upstash sin avisar al equipo.

La cuenta de Google Cloud está en **prueba gratuita**: tiene un crédito de 1.001.736 pesos (unos 300 USD) que vence el **9 de enero de 2027**. Lo que no tiene capa gratuita (por ejemplo, las métricas de Managed Service for Prometheus) se descuenta de ese crédito y no de una tarjeta. Cuando la prueba termine, o si el crédito se agota antes, los servicios de Google Cloud se pausan hasta que alguien actualice a una cuenta de pago.

## Pendiente

- Automatizar la publicación del frontend (hoy es manual, desde Cloud Shell): una acción de GitHub que compile y publique en Firebase Hosting.
- Avisos de seguridad en las librerías del frontend: `npm ci` reportó 23 vulnerabilidades (17 moderadas y 6 altas). El escaneo de Trivy del Pull Request puede bloquear por las altas con corrección disponible: revisar con `npm audit` y actualizar lo necesario.
- Pull Request de la rama de integración hacia `main`: la integración continua corre las pruebas, Sonar y el escaneo de seguridad de Trivy, que bloquea si encuentra vulnerabilidades graves con corrección disponible. Después, el despliegue y la primera prueba real en Cloud Run (arranque, memoria y tiempo de inicio).
- Probar Neon, Firebase, R2 y el servidor de parámetros desde la aplicación desplegada.
- Strapi (a cargo de Anderson): servidor, colecciones, mensajes, plantilla del correo y token. Sin él, los mensajes salen como códigos y no se envía el correo de la autorización. Cuando exista, agregar `STRAPI_URL` y el secreto `STRAPI_API_TOKEN` (la caché en Redis ya está configurada).
- Probar la caché de Redis desde la aplicación desplegada (hoy solo se probó la conexión desde Cloud Shell).
- Página de destino del QR en el frontend (`/visitas/autorizaciones/<id>`).
- Registro de usuarios en el backend (caso de uso aparte): hoy los usuarios nuevos se crean a mano en Neon.
- Cambiar la contraseña de `smartlife_app` en Neon (y agregar la nueva como versión de `db-password`) antes de cargar datos reales.
- Telemetría (ADR-0030): el código de OpenTelemetry está conectado al caso de uso de autorizar visita (autorizaciones creadas y fallidas, correos no enviados y duración) y apagado por defecto. Están listos el collector (`infra/otel-collector`: configuración y Dockerfile), las APIs de Cloud Monitoring y Cloud Trace y los roles `roles/monitoring.metricWriter` y `roles/cloudtrace.agent` de `smartlife-runtime`. Falta construir la imagen del collector en la integración continua, desplegar Cloud Run con dos contenedores (la aplicación y el collector) y encender `OTLP_METRICS_ENABLED` y `OTLP_TRACING_ENABLED`; las direcciones por defecto ya apuntan al collector (`localhost:4318`). Las trazas tienen capa gratuita en Cloud Trace; las métricas de Managed Service for Prometheus cobran por muestra y se descuentan del crédito de prueba.