package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import java.net.URI;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.VisitParametersOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

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

		final Optional<Duration> maxAuthorizationDuration = required(binder, "max-authorization-duration", Duration.class, missing);
		final Optional<Duration> maxAnticipation = required(binder, "max-anticipation", Duration.class, missing);
		final Optional<Duration> startTolerance = required(binder, "start-tolerance", Duration.class, missing);
		final Optional<DataSize> documentMaxSize = required(binder, "document.max-size", DataSize.class, missing);
		final List<String> allowedTypes = binder.bind(PREFIX + "document.allowed-types", Bindable.listOf(String.class))
				.orElse(List.of());
		if (allowedTypes.isEmpty()) {
			missing.add(PREFIX + "document.allowed-types");
		}
		final Optional<String> qrBaseUrl = required(binder, "qr-base-url", String.class, missing);
		final Optional<String> timeZone = required(binder, "time-zone", String.class, missing);

		mustBePositive("max-authorization-duration", maxAuthorizationDuration, missing);
		mustBePositive("max-anticipation", maxAnticipation, missing);
		startTolerance.filter(Duration::isNegative)
				.ifPresent(value -> missing.add(PREFIX + "start-tolerance (no puede ser negativo)"));
		documentMaxSize.filter(size -> size.toBytes() <= 0)
				.ifPresent(size -> missing.add(PREFIX + "document.max-size (debe ser mayor que cero)"));
		qrBaseUrl.filter(url -> !isAbsoluteHttpUrl(url))
				.ifPresent(url -> missing.add(PREFIX + "qr-base-url (debe ser una URL http o https completa)"));
		final Optional<ZoneId> zone = zoneOf(timeZone, missing);

		if (!missing.isEmpty()) {
			LOGGER.error("Faltan o son invalidos parametros de visitas en el servidor de configuracion: {}", missing);
			throw new IllegalStateException(
					"Faltan o son invalidos parametros de visitas en el servidor de configuracion: " + missing);
		}
		return new VisitParametersDTO(maxAuthorizationDuration.orElseThrow(), maxAnticipation.orElseThrow(),
				startTolerance.orElseThrow(), documentMaxSize.orElseThrow().toBytes(), normalizedTypes(allowedTypes),
				qrBaseUrl.orElseThrow(), zone.orElseThrow());
	}

	private static Set<String> normalizedTypes(final List<String> allowedTypes) {
		return allowedTypes.stream().map(type -> type.trim().toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
	}

	private static void mustBePositive(final String name, final Optional<Duration> value, final List<String> missing) {
		value.filter(duration -> duration.isZero() || duration.isNegative())
				.ifPresent(duration -> missing.add(PREFIX + name + " (debe ser mayor que cero)"));
	}

	private static boolean isAbsoluteHttpUrl(final String value) {
		try {
			final URI uri = URI.create(value.trim());
			return !ObjectHelper.isNull(uri.getHost())
					&& ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()));
		} catch (final IllegalArgumentException exception) {
			return false;
		}
	}

	private static Optional<ZoneId> zoneOf(final Optional<String> timeZone, final List<String> missing) {
		if (timeZone.isEmpty()) {
			return Optional.empty();
		}
		try {
			return Optional.of(ZoneId.of(timeZone.get().trim()));
		} catch (final DateTimeException exception) {
			missing.add(PREFIX + "time-zone (zona horaria desconocida: '" + timeZone.get() + "')");
			return Optional.empty();
		}
	}

	private static <T> Optional<T> required(final Binder binder, final String name, final Class<T> type,
			final List<String> missing) {
		final BindResult<T> result;
		try {
			result = binder.bind(PREFIX + name, Bindable.of(type));
		} catch (final BindException exception) {
			missing.add(PREFIX + name + " (valor invalido)");
			return Optional.empty();
		}
		if (!result.isBound() || (result.get() instanceof String text && text.isBlank())) {
			missing.add(PREFIX + name);
			return Optional.empty();
		}
		return Optional.of(result.get());
	}
}
