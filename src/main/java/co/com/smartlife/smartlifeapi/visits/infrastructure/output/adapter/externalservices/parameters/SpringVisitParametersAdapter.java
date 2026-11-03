package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import java.net.URI;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.VisitParametersOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

/**
 * Lee los parametros {@code smartlife.visits.*} del {@link Environment}, que Spring Cloud Config
 * alimenta. Se enlazan en cada consulta para respetar los valores vigentes tras un refresh.
 * <p>
 * No hay valores por defecto en el codigo: si el servidor de parametros no entrego alguno, o entrego un valor que no
 * tiene sentido (una duracion negativa, una zona horaria desconocida, una URL relativa), falla con un mensaje que
 * lista exactamente cuales son, en vez de seguir en silencio con valores inventados.
 */
@Component
public class SpringVisitParametersAdapter implements VisitParametersOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringVisitParametersAdapter.class);
	private static final String PREFIX = "smartlife.visits.";

	private final Environment environment;

	public SpringVisitParametersAdapter(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public VisitParametersDTO get() {
		final Binder binder = Binder.get(environment);
		final List<String> missing = new ArrayList<>();

		final Duration maxAuthorizationDuration = required(binder, "max-authorization-duration", Duration.class, missing);
		final Duration maxAnticipation = required(binder, "max-anticipation", Duration.class, missing);
		final Duration startTolerance = required(binder, "start-tolerance", Duration.class, missing);
		final DataSize documentMaxSize = required(binder, "document.max-size", DataSize.class, missing);
		final List<String> allowedTypes = binder.bind(PREFIX + "document.allowed-types", Bindable.listOf(String.class))
				.orElse(List.of());
		if (allowedTypes.isEmpty()) {
			missing.add(PREFIX + "document.allowed-types");
		}
		final String qrBaseUrl = required(binder, "qr-base-url", String.class, missing);
		final String timeZone = required(binder, "time-zone", String.class, missing);

		mustBePositive("max-authorization-duration", maxAuthorizationDuration, missing);
		mustBePositive("max-anticipation", maxAnticipation, missing);
		if (startTolerance != null && startTolerance.isNegative()) {
			missing.add(PREFIX + "start-tolerance (no puede ser negativo)");
		}
		if (documentMaxSize != null && documentMaxSize.toBytes() <= 0) {
			missing.add(PREFIX + "document.max-size (debe ser mayor que cero)");
		}
		if (qrBaseUrl != null && !isAbsoluteHttpUrl(qrBaseUrl)) {
			missing.add(PREFIX + "qr-base-url (debe ser una URL http o https completa)");
		}
		final ZoneId zone = zoneOrNull(timeZone, missing);

		if (!missing.isEmpty()) {
			LOGGER.error("Faltan o son invalidos parametros de visitas en el servidor de configuracion: {}", missing);
			throw new IllegalStateException(
					"Faltan o son invalidos parametros de visitas en el servidor de configuracion: " + missing);
		}

		return new VisitParametersDTO(maxAuthorizationDuration, maxAnticipation, startTolerance,
				documentMaxSize.toBytes(), normalizedTypes(allowedTypes), qrBaseUrl, zone);
	}

	/** Los tipos se comparan en minusculas porque asi llega el tipo del documento adjunto. */
	private static Set<String> normalizedTypes(final List<String> allowedTypes) {
		return allowedTypes.stream().map(type -> type.trim().toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
	}

	private static void mustBePositive(final String name, final Duration value, final List<String> missing) {
		if (value != null && (value.isZero() || value.isNegative())) {
			missing.add(PREFIX + name + " (debe ser mayor que cero)");
		}
	}

	private static boolean isAbsoluteHttpUrl(final String value) {
		try {
			final URI uri = URI.create(value.trim());
			return uri.getHost() != null && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
		} catch (final IllegalArgumentException exception) {
			return false;
		}
	}

	private static ZoneId zoneOrNull(final String timeZone, final List<String> missing) {
		if (timeZone == null) {
			return null;   // ya se reporto que falta
		}
		try {
			return ZoneId.of(timeZone.trim());
		} catch (final DateTimeException exception) {
			missing.add(PREFIX + "time-zone (zona horaria desconocida: '" + timeZone + "')");
			return null;
		}
	}

	/** Devuelve el valor, o lo agrega a la lista de faltantes si no llego o esta vacio. */
	private static <T> T required(final Binder binder, final String name, final Class<T> type,
			final List<String> missing) {
		final T value;
		try {
			value = binder.bind(PREFIX + name, Bindable.of(type)).orElse(null);
		} catch (final BindException exception) {
			missing.add(PREFIX + name + " (valor invalido)");
			return null;
		}
		if (value == null || (value instanceof String text && text.isBlank())) {
			missing.add(PREFIX + name);
			return null;
		}
		return value;
	}

}