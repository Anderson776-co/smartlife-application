package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.springframework.util.unit.DataSize;

/**
 * Parametros de negocio de las visitas. Vienen del servidor de parametros
 * (Spring Cloud Config) y, si este no responde, de los valores de respaldo.
 */
public record VisitParameters(Duration maxAuthorizationDuration, Duration maxAnticipation, Duration startTolerance,
		DataSize documentMaxSize, List<String> documentAllowedTypes, String qrBaseUrl, ZoneId timeZone) {

	public VisitParameters {
		requirePositive(maxAuthorizationDuration, "max-authorization-duration");
		requirePositive(maxAnticipation, "max-anticipation");
		requirePositive(startTolerance, "start-tolerance");
		Objects.requireNonNull(documentMaxSize, "smartlife.visits.document.max-size es obligatorio");
		if (documentMaxSize.toBytes() <= 0) {
			throw new IllegalArgumentException("smartlife.visits.document.max-size debe ser mayor que cero");
		}
		Objects.requireNonNull(documentAllowedTypes, "smartlife.visits.document.allowed-types es obligatorio");
		if (documentAllowedTypes.isEmpty()) {
			throw new IllegalArgumentException("smartlife.visits.document.allowed-types no puede estar vacio");
		}
		documentAllowedTypes = List.copyOf(documentAllowedTypes);
		if (qrBaseUrl == null || qrBaseUrl.isBlank()) {
			throw new IllegalArgumentException("smartlife.visits.qr-base-url no puede estar vacio");
		}
		Objects.requireNonNull(timeZone, "smartlife.visits.time-zone es obligatorio");
	}

	/** Valores de respaldo: los mismos que trae application.properties. */
	public static VisitParameters defaults() {
		return new VisitParameters(Duration.ofHours(24), Duration.ofDays(30), Duration.ofMinutes(10),
				DataSize.ofMegabytes(5), List.of("application/pdf", "image/jpeg", "image/png"),
				"http://localhost:5173/visitas/autorizaciones/", ZoneId.of("UTC"));
	}

	private static void requirePositive(final Duration value, final String name) {
		Objects.requireNonNull(value, "smartlife.visits." + name + " es obligatorio");
		if (value.isZero() || value.isNegative()) {
			throw new IllegalArgumentException("smartlife.visits." + name + " debe ser mayor que cero");
		}
	}
}