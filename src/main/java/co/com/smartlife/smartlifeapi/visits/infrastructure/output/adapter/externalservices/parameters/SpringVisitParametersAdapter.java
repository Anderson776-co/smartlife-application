package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.parameters;

import java.time.Duration;
import java.time.ZoneId;
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
 */
@Component
public class SpringVisitParametersAdapter implements VisitParametersOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringVisitParametersAdapter.class);
	private static final String PREFIX = "smartlife.visits.";
	private static final List<String> DEFAULT_ALLOWED_TYPES = List.of("application/pdf", "image/jpeg", "image/png");

	private final Environment environment;

	public SpringVisitParametersAdapter(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public VisitParametersDTO get() {
		final Binder binder = Binder.get(environment);
		return new VisitParametersDTO(
				bind(binder, "max-authorization-duration", Duration.class, Duration.ofHours(24)),
				bind(binder, "max-anticipation", Duration.class, Duration.ofDays(30)),
				bind(binder, "start-tolerance", Duration.class, Duration.ofMinutes(10)),
				bind(binder, "document.max-size", DataSize.class, DataSize.ofMegabytes(5)).toBytes(),
				Set.copyOf(binder.bind(PREFIX + "document.allowed-types", Bindable.listOf(String.class))
						.orElseGet(() -> usingDefault("document.allowed-types", DEFAULT_ALLOWED_TYPES))),
				bind(binder, "qr-base-url", String.class, "http://localhost:5173/visitas/autorizaciones/"),
				ZoneId.of(bind(binder, "time-zone", String.class, "UTC")));
	}

	private static <T> T bind(final Binder binder, final String name, final Class<T> type, final T defaultValue) {
		return binder.bind(PREFIX + name, Bindable.of(type)).orElseGet(() -> usingDefault(name, defaultValue));
	}

	/**
	 * Si un parametro no llega del servidor de configuracion (por ejemplo, si estaba dormido al arrancar), la
	 * aplicacion sigue con el valor por defecto del codigo. Se avisa en el registro para que no pase desapercibido.
	 */
	private static <T> T usingDefault(final String name, final T defaultValue) {
		LOGGER.warn("El parametro {}{} no llego del servidor de configuracion: se usa el valor por defecto del codigo ({})",
				PREFIX, name, defaultValue);
		return defaultValue;
	}

}