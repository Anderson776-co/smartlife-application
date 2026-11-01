# Collector de OpenTelemetry (version fijada, la misma del ejemplo oficial de Google para Cloud Run).
FROM otel/opentelemetry-collector-contrib:0.121.0

COPY --chmod=644 collector-config.yaml /etc/otelcol-contrib/config.yaml

# Sin permisos de administrador (el escaneo de seguridad de Trivy lo exige).
USER 10001