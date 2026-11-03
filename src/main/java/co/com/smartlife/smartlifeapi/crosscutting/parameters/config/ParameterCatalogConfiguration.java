package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import jakarta.annotation.PostConstruct;

/**
 * Llena el {@link ParameterCatalog} con las llaves {@code smartlife.visits.validation.*} del servidor de
 * configuracion, al arrancar y cada vez que se refrescan los parametros (webhook del Config Server).
 * <p>
 * Arrancar no depende de que esten: si falta o es invalida alguna llave se registra el error con la lista exacta y
 * {@link ParameterCatalog#ensureLoaded()} falla al usarlas. Si un refresco llega incompleto se conservan los valores
 * anteriores, para que un error de configuracion no tumbe lo que ya funcionaba.
 */
@Configuration
public class ParameterCatalogConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterCatalogConfiguration.class);
	private static final String FORMAT = "smartlife.visits.validation.format.";
	private static final String LENGTH = "smartlife.visits.validation.length.";

	private final Environment environment;

	public ParameterCatalogConfiguration(final Environment environment) {
		this.environment = environment;
	}

	@EventListener(EnvironmentChangeEvent.class)
	public void onParametersRefreshed() {
		loadParameters();
	}

	@PostConstruct
	public void loadParameters() {
		final List<String> missing = new ArrayList<>();

		final String documentNumberPattern = pattern(FORMAT + "document-number", missing);
		final Integer documentNumberMin = number(LENGTH + "document-number-min", missing);
		final Integer documentNumberMax = number(LENGTH + "document-number-max", missing);

		final String firstNamePattern = pattern(FORMAT + "first-name", missing);
		final Integer firstNameMin = number(LENGTH + "first-name-min", missing);
		final Integer firstNameMax = number(LENGTH + "first-name-max", missing);

		final String middleNamePattern = pattern(FORMAT + "middle-name", missing);
		final Integer middleNameMin = number(LENGTH + "middle-name-min", missing);
		final Integer middleNameMax = number(LENGTH + "middle-name-max", missing);

		final String lastNamePattern = pattern(FORMAT + "last-name", missing);
		final Integer lastNameMin = number(LENGTH + "last-name-min", missing);
		final Integer lastNameMax = number(LENGTH + "last-name-max", missing);

		final String secondLastNamePattern = pattern(FORMAT + "second-last-name", missing);
		final Integer secondLastNameMin = number(LENGTH + "second-last-name-min", missing);
		final Integer secondLastNameMax = number(LENGTH + "second-last-name-max", missing);

		final String emailPattern = pattern(FORMAT + "email", missing);
		final Integer emailMin = number(LENGTH + "email-min", missing);
		final Integer emailMax = number(LENGTH + "email-max", missing);

		final String vehiclePlatePattern = pattern(FORMAT + "vehicle-plate", missing);
		final Integer vehiclePlateMin = number(LENGTH + "vehicle-plate-min", missing);
		final Integer vehiclePlateMax = number(LENGTH + "vehicle-plate-max", missing);

		final Integer observationMax = number(LENGTH + "observation-max", missing);
		final Map<String, byte[]> documentSignatures = signatures(FORMAT + "document-signatures", missing);

		range(LENGTH + "document-number", documentNumberMin, documentNumberMax, missing);
		range(LENGTH + "first-name", firstNameMin, firstNameMax, missing);
		range(LENGTH + "middle-name", middleNameMin, middleNameMax, missing);
		range(LENGTH + "last-name", lastNameMin, lastNameMax, missing);
		range(LENGTH + "second-last-name", secondLastNameMin, secondLastNameMax, missing);
		range(LENGTH + "email", emailMin, emailMax, missing);
		range(LENGTH + "vehicle-plate", vehiclePlateMin, vehiclePlateMax, missing);
		if (observationMax != null && observationMax < 1) {
			missing.add(LENGTH + "observation-max (debe ser al menos 1)");
		}

		if (!missing.isEmpty()) {
			LOGGER.error("Faltan o son invalidos parametros de validacion en el servidor de configuracion: {}", missing);
			if (!ParameterCatalog.isLoaded()) {
				ParameterCatalog.markLoaded(missing);
			}
			return;
		}

		ParameterCatalog.DOCUMENT_NUMBER_PATTERN = documentNumberPattern;
		ParameterCatalog.DOCUMENT_NUMBER_MIN_LENGTH = documentNumberMin;
		ParameterCatalog.DOCUMENT_NUMBER_MAX_LENGTH = documentNumberMax;

		ParameterCatalog.FIRST_NAME_PATTERN = firstNamePattern;
		ParameterCatalog.FIRST_NAME_MIN_LENGTH = firstNameMin;
		ParameterCatalog.FIRST_NAME_MAX_LENGTH = firstNameMax;

		ParameterCatalog.MIDDLE_NAME_PATTERN = middleNamePattern;
		ParameterCatalog.MIDDLE_NAME_MIN_LENGTH = middleNameMin;
		ParameterCatalog.MIDDLE_NAME_MAX_LENGTH = middleNameMax;

		ParameterCatalog.LAST_NAME_PATTERN = lastNamePattern;
		ParameterCatalog.LAST_NAME_MIN_LENGTH = lastNameMin;
		ParameterCatalog.LAST_NAME_MAX_LENGTH = lastNameMax;

		ParameterCatalog.SECOND_LAST_NAME_PATTERN = secondLastNamePattern;
		ParameterCatalog.SECOND_LAST_NAME_MIN_LENGTH = secondLastNameMin;
		ParameterCatalog.SECOND_LAST_NAME_MAX_LENGTH = secondLastNameMax;

		ParameterCatalog.EMAIL_PATTERN = emailPattern;
		ParameterCatalog.EMAIL_MIN_LENGTH = emailMin;
		ParameterCatalog.EMAIL_MAX_LENGTH = emailMax;

		ParameterCatalog.VEHICLE_PLATE_PATTERN = vehiclePlatePattern;
		ParameterCatalog.VEHICLE_PLATE_MIN_LENGTH = vehiclePlateMin;
		ParameterCatalog.VEHICLE_PLATE_MAX_LENGTH = vehiclePlateMax;

		ParameterCatalog.OBSERVATION_MAX_LENGTH = observationMax;
		ParameterCatalog.DOCUMENT_SIGNATURES = documentSignatures;

		ParameterCatalog.markLoaded(List.of());
		LOGGER.info("Parametros de validacion cargados desde el servidor de configuracion");
	}

	/** Una expresion regular: debe existir y compilar. */
	private String pattern(final String key, final List<String> missing) {
		final String value = environment.getProperty(key);
		if (value == null || value.isBlank()) {
			missing.add(key);
			return null;
		}
		try {
			Pattern.compile(value);
			return value;
		} catch (final PatternSyntaxException exception) {
			missing.add(key + " (expresion regular invalida)");
			return null;
		}
	}

	/** Un numero entero: debe existir y serlo. */
	private Integer number(final String key, final List<String> missing) {
		final String value = environment.getProperty(key);
		if (value == null || value.isBlank()) {
			missing.add(key);
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (final NumberFormatException exception) {
			missing.add(key + " (no es un numero entero)");
			return null;
		}
	}

	/** Un largo minimo y maximo coherentes: el minimo no es negativo y el maximo es al menos 1 y no menor que el minimo. */
	private static void range(final String keyPrefix, final Integer min, final Integer max, final List<String> missing) {
		if (min == null || max == null) {
			return;   // ya se reporto que falta o no es un numero
		}
		if (min < 0) {
			missing.add(keyPrefix + "-min (no puede ser negativo)");
		}
		if (max < 1 || max < min) {
			missing.add(keyPrefix + "-max (debe ser al menos 1 y no menor que el minimo)");
		}
	}

	/**
	 * Las firmas, como {@code tipo=HEX,tipo=HEX} (por ejemplo {@code application/pdf=25504446}). El tipo se guarda en
	 * minusculas, que es como llega el tipo del documento adjunto.
	 */
	private Map<String, byte[]> signatures(final String key, final List<String> missing) {
		final String value = environment.getProperty(key);
		if (value == null || value.isBlank()) {
			missing.add(key);
			return null;
		}
		final Map<String, byte[]> signatures = new LinkedHashMap<>();
		for (final String entry : value.split(",")) {
			final String[] parts = entry.trim().split("=", 2);
			if (parts.length != 2 || parts[0].isBlank() || !parts[1].trim().matches("(?i)([0-9a-f]{2})+")) {
				missing.add(key + " (entrada invalida: '" + entry.trim() + "')");
				return null;
			}
			signatures.put(parts[0].trim().toLowerCase(Locale.ROOT), hexToBytes(parts[1].trim()));
		}
		return Map.copyOf(signatures);
	}

	private static byte[] hexToBytes(final String hex) {
		final byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] = (byte) Integer.parseInt(hex.substring(2 * index, 2 * index + 2), 16);
		}
		return bytes;
	}

}
