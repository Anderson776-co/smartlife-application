package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * No hay valores por defecto en el codigo: si el servidor de parametros no entrego alguno, falla con un
 * mensaje que lista exactamente cuales faltan, en vez de seguir en silencio con valores inventados.
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

		if (!missing.isEmpty()) {
			LOGGER.error("Faltan parametros de visitas en el servidor de configuracion: {}", missing);
			throw new IllegalStateException(
					"Faltan parametros de visitas en el servidor de configuracion: " + missing);
		}

		return new VisitParametersDTO(maxAuthorizationDuration, maxAnticipation, startTolerance,
				documentMaxSize.toBytes(), Set.copyOf(allowedTypes), qrBaseUrl, ZoneId.of(timeZone));
	}

	/** Devuelve el valor, o lo agrega a la lista de faltantes si no llego o esta vacio. */
	private static <T> T required(final Binder binder, final String name, final Class<T> type,
			final List<String> missing) {
		final T value = binder.bind(PREFIX + name, Bindable.of(type)).orElse(null);
		if (value == null || (value instanceof String text && text.isBlank())) {
			missing.add(PREFIX + name);
			return null;
		}
		return value;
	}

}