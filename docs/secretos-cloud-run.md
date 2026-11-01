
## Webhooks entrantes (mantienen al día lo que el backend guarda en memoria)

El backend recibe dos webhooks para no tener que reiniciarse cuando cambia un texto de Strapi o un parámetro. Los dos son `POST`, no usan sesión ni cookie y se autentican con un secreto. Si el secreto no está configurado, el backend rechaza todo (falla cerrado), y las comparaciones son de tiempo constante.

| Webhook | Ruta | Quién lo envía | Cómo se autentica | Qué hace |
|---|---|---|---|---|
| Strapi | `/api/v1/internal/webhooks/strapi` | Strapi, al publicar o cambiar un mensaje o una plantilla | Cabecera `X-Webhook-Secret` con el valor de `strapi-webhook-secret` | Borra de la caché (Redis) el texto que cambió, para que se vuelva a pedir a Strapi |
| Parámetros | `/api/v1/internal/webhooks/config` | GitHub, con un `push` al repositorio de parámetros | Firma HMAC-SHA256 del cuerpo en `X-Hub-Signature-256`, calculada con `config-webhook-secret` | Si el `push` es a la rama vigilada (`CONFIG_WEBHOOK_REF`, por defecto `refs/heads/main`), vuelve a leer los parámetros del servidor de configuración, sin reiniciar; los demás eventos y ramas se ignoran |

- Las dos rutas llegan por Cloudflare, el Worker y el API Gateway (configuración activa `smartlife-config-3`, con las siete rutas) y están en el contrato `infra/api-gateway/openapi2.yaml`. El WAF y el límite de peticiones de Cloudflare no las bloquean: la entrega de GitHub llegó hasta el gateway.
- Seguridad: los webhooks tienen su propia cadena de filtros de Spring Security (`webhookFilterChain`, sin estado, sin el filtro de la cookie de sesión, que rechaza cualquier otra ruta). En esa cadena se desactiva CSRF, porque estas peticiones no dependen de una sesión del navegador; en el resto de la API CSRF sigue completo. La regla S4502 de Sonar se declara aceptada en el código con su justificación.
- **Webhook de GitHub (creado el 1 de noviembre de 2026 por Mateo, propietario del repositorio):** `mateoguerramarin-dotcom/Parameter-Catalog-SmartLife` > Settings > Webhooks. URL `https://api.smartlife.sarl/api/v1/internal/webhooks/config`, tipo `application/json`, verificación SSL activada, solo el evento `push`, activo, y como secreto el valor de `config-webhook-secret`. El `ping` inicial dio *timeout* (algo pasajero) y la reentrega dio `404` en 0,39 s, que era lo esperado porque la aplicación nueva todavía no estaba desplegada.
- El servidor de parámetros lee los valores directamente de ese repositorio (rama `main`), así que un `push` a `main` cambia lo que sirve; el webhook avisa al backend para que los recargue. Cualquier `push` a `main` del repositorio dispara la recarga, aunque toque el `Dockerfile` o el código del servidor: es inofensivo (recarga los mismos valores).
- Cómo comprobarlo, una vez desplegada la aplicación nueva: en Recent Deliveries del webhook, **Redeliver** del `ping` debe dar `200`. Después, un cambio real de un valor de `smartlife-api.yaml` en `main` debe dar `200` o `202`, y el registro del backend debe decir «Parámetros recargados». Si da `401`, el secreto del webhook no coincide con el del backend; si da `403`, hay que revisar las reglas del WAF de Cloudflare; si da *timeout*, la aplicación estaba dormida (GitHub espera 10 segundos): reentregar.
- Si se cambia el valor de un secreto de webhook, hay que crear una versión nueva en Secret Manager, actualizar el secreto en el emisor (GitHub o Strapi) y reiniciar o redesplegar la aplicación, porque las instancias ya encendidas conservan el valor que leyeron al arrancar.

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

- API `smartlife-api`, gateway `smartlife-gateway` en `us-central1` y configuración activa `smartlife-config-3` (comprobado el 1 de noviembre de 2026; trae las siete rutas de abajo). Dirección: `smartlife-gateway-29p679a1.uc.gateway.dev`.
- El contrato (Swagger 2.0) está en `infra/api-gateway/openapi2.yaml`. Solo define las rutas que expone la aplicación; cualquier otra ruta recibe `404` del gateway:
  - `GET /api/v1/users/sessions/csrf`
  - `POST /api/v1/users/sessions`
  - `GET /api/v1/users/sessions/me`
  - `POST /api/v1/users/sessions/logout`
  - `POST /api/v1/visits/authorizations`
  - `POST /api/v1/internal/webhooks/strapi` y `POST /api/v1/internal/webhooks/config` (webhooks entrantes, ver su sección)
  - y la verificación previa de CORS (`OPTIONS`) de las cinco primeras. El CORS lo resuelve la propia aplicación, no el gateway.
- Plazo máximo de respuesta del gateway: 60 segundos (la primera petición a Cloud Run "frío" puede tardar).
- Cuenta de servicio del gateway: `smartlife-gateway@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com`, con el rol `roles/run.invoker` **solo sobre el servicio** `smartlife-application`. El servicio de Cloud Run sigue siendo privado (no tiene `allUsers`).
- Clave de API: `smartlife-worker-cloudflare`, limitada solo al servicio del gateway. Sin esa clave, el gateway responde `401`.
- Probado el 29 de octubre de 2026 con `curl`:
  - `api.smartlife.sarl`: llega a la aplicación (la versión desplegada hoy viene de `main` y no tiene todavía las rutas nuevas, por eso responde `404` de Spring).
  - Gateway sin clave: `401`. `run.app` directo: `403`. `workers.dev`: `404`.
  - `/actuator/health` por `api.smartlife.sarl`: lo bloquea el WAF.
- **Si se agrega una ruta a la aplicación**, hay que agregarla al contrato y crear una configuración nueva (el ejemplo usa `smartlife-config-4`, la siguiente a la activa; antes se ve cuál existe con `gcloud api-gateway api-configs list --api=smartlife-api`):

        gcloud api-gateway api-configs create smartlife-config-4 --api=smartlife-api --openapi-spec=infra/api-gateway/openapi2.yaml --backend-auth-service-account=smartlife-gateway@project-078c9c72-e891-42db-98c.iam.gserviceaccount.com --project=project-078c9c72-e891-42db-98c
        gcloud api-gateway gateways update smartlife-gateway --api=smartlife-api --api-config=smartlife-config-4 --location=us-central1 --project=project-078c9c72-e891-42db-98c

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
| `STRAPI_URL` | `https://strapi-cms-clfarpgyxa-uc.a.run.app` (definida en el `cicd.yml`; Strapi está a cargo de Anderson) |
| `CACHE_TYPE` | `redis` (definida en el `cicd.yml`; `none` apaga la caché y `simple` usa una caché en memoria, sin vencimiento) |
| `CACHE_TTL` | `1h` |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_SSL` | `rapid-sculpin-44456.upstash.io`, `6379`, `default`, `true` (definidas en el `cicd.yml`) |
| `CONFIG_WEBHOOK_REF` | `refs/heads/main` (por defecto): la rama del repositorio de parámetros cuyo `push` recarga los parámetros |
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
      STRAPI_URL=<direccion de Strapi en Cloud Run>
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
      STRAPI_API_TOKEN=strapi-api-token:latest
      REDIS_PASSWORD=redis-password:latest
      STRAPI_WEBHOOK_SECRET=strapi-webhook-secret:latest
      CONFIG_WEBHOOK_SECRET=config-webhook-secret:latest
    secrets_update_strategy: merge

El despliegue solo corre con un push a `main`. El servicio se despliega con `--memory=1Gi --max-instances=1 --cpu-boost` (más memoria que la de por defecto, una sola instancia para quedar dentro de lo gratuito y más CPU durante el arranque de Spring Boot).

## Qué hacer cuando llega una clave

**Paso 1.** En Secret Manager, abrir el secreto (por ejemplo `otro-token`), pestaña **Versiones**, **Versión nueva**, pegar la clave y guardar. La clave se escribe **solo ahí**.

**Paso 2.** Agregar su línea al bloque `secrets` del paso `Deploy`. Por ejemplo, para un secreto nuevo (`otro-token`) quedaría:

    secrets: |-
      RESEND_API_KEY=resend-api-key:latest
      DB_PASSWORD=db-password:latest
      R2_ACCESS_KEY=r2-access-key:latest
      R2_SECRET_KEY=r2-secret-key:latest
      OTRO_TOKEN=otro-token:latest
    secrets_update_strategy: merge

**Paso 3.** Agregar como variables normales las que acompañan a esa clave (por ejemplo, la dirección del servicio al que pertenece la clave).

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
- Strapi (a cargo de Anderson): ya está desplegado en Cloud Run y conectado (`STRAPI_URL` y el secreto `STRAPI_API_TOKEN`). Falta confirmar que las colecciones tienen los mensajes y la plantilla `VISIT_AUTHORIZATION_CREATED` publicados, guardar una copia de los JSON (hoy solo están en el computador de Anderson), anotar dónde está la base PostgreSQL y su plan, revisar que el servicio de Strapi no tenga instancias mínimas encendidas (para no gastar la capa gratuita de Cloud Run) y probar el correo desde la aplicación desplegada.
- Probar la caché de Redis desde la aplicación desplegada (hoy solo se probó la conexión desde Cloud Shell).
- Página de destino del QR en el frontend (`/visitas/autorizaciones/<id>`).
- Registro de usuarios en el backend (caso de uso aparte): hoy los usuarios nuevos se crean a mano en Neon.
- Cambiar la contraseña de `smartlife_app` en Neon (y agregar la nueva como versión de `db-password`) antes de cargar datos reales.
- Webhooks: tras el primer despliegue de la aplicación nueva, **reentregar** el `ping` del webhook de GitHub (debe dar `200`) y hacer una prueba real cambiando un valor de `smartlife-api.yaml`. Anderson debe registrar el webhook de Strapi (ruta `/strapi`, cabecera `X-Webhook-Secret`) y confirmar que `strapi-webhook-secret` tiene versión y permiso para `smartlife-runtime`.
- Rotar `config-webhook-secret` antes de cargar datos reales: su valor se mostró en una conversación de trabajo el 1 de noviembre de 2026. Se crea una versión nueva, se desactiva la anterior y se actualiza el secreto del webhook de GitHub (Settings > Webhooks > Edit > Change secret).
- Telemetría (ADR-0030): el código de OpenTelemetry está conectado al caso de uso de autorizar visita (autorizaciones creadas y fallidas, correos no enviados y duración) y apagado por defecto. Están listos el collector (`infra/otel-collector`: configuración y Dockerfile), las APIs de Cloud Monitoring y Cloud Trace y los roles `roles/monitoring.metricWriter` y `roles/cloudtrace.agent` de `smartlife-runtime`. Falta construir la imagen del collector en la integración continua, desplegar Cloud Run con dos contenedores (la aplicación y el collector) y encender `OTLP_METRICS_ENABLED` y `OTLP_TRACING_ENABLED`; las direcciones por defecto ya apuntan al collector (`localhost:4318`). Las trazas tienen capa gratuita en Cloud Trace; las métricas de Managed Service for Prometheus cobran por muestra y se descuentan del crédito de prueba.