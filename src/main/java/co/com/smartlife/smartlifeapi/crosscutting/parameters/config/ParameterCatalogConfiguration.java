package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import jakarta.annotation.PostConstruct;

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
		load(environment);
	}

	private static void load(final Environment environment) {
		final List<String> missing = new ArrayList<>();

		final Optional<String> documentNumberPattern = pattern(environment, FORMAT + "document-number", missing);
		final Optional<Integer> documentNumberMin = number(environment, LENGTH + "document-number-min", missing);
		final Optional<Integer> documentNumberMax = number(environment, LENGTH + "document-number-max", missing);

		final Optional<String> firstNamePattern = pattern(environment, FORMAT + "first-name", missing);
		final Optional<Integer> firstNameMin = number(environment, LENGTH + "first-name-min", missing);
		final Optional<Integer> firstNameMax = number(environment, LENGTH + "first-name-max", missing);

		final Optional<String> middleNamePattern = pattern(environment, FORMAT + "middle-name", missing);
		final Optional<Integer> middleNameMin = number(environment, LENGTH + "middle-name-min", missing);
		final Optional<Integer> middleNameMax = number(environment, LENGTH + "middle-name-max", missing);

		final Optional<String> lastNamePattern = pattern(environment, FORMAT + "last-name", missing);
		final Optional<Integer> lastNameMin = number(environment, LENGTH + "last-name-min", missing);
		final Optional<Integer> lastNameMax = number(environment, LENGTH + "last-name-max", missing);

		final Optional<String> secondLastNamePattern = pattern(environment, FORMAT + "second-last-name", missing);
		final Optional<Integer> secondLastNameMin = number(environment, LENGTH + "second-last-name-min", missing);
		final Optional<Integer> secondLastNameMax = number(environment, LENGTH + "second-last-name-max", missing);

		final Optional<String> emailPattern = pattern(environment, FORMAT + "email", missing);
		final Optional<Integer> emailMin = number(environment, LENGTH + "email-min", missing);
		final Optional<Integer> emailMax = number(environment, LENGTH + "email-max", missing);

		final Optional<String> vehiclePlatePattern = pattern(environment, FORMAT + "vehicle-plate", missing);
		final Optional<Integer> vehiclePlateMin = number(environment, LENGTH + "vehicle-plate-min", missing);
		final Optional<Integer> vehiclePlateMax = number(environment, LENGTH + "vehicle-plate-max", missing);

		final Optional<Integer> observationMax = number(environment, LENGTH + "observation-max", missing);
		final Optional<Map<String, byte[]>> documentSignatures = signatures(environment, FORMAT + "document-signatures", missing);

		range(LENGTH + "document-number", documentNumberMin, documentNumberMax, missing);
		range(LENGTH + "first-name", firstNameMin, firstNameMax, missing);
		range(LENGTH + "middle-name", middleNameMin, middleNameMax, missing);
		range(LENGTH + "last-name", lastNameMin, lastNameMax, missing);
		range(LENGTH + "second-last-name", secondLastNameMin, secondLastNameMax, missing);
		range(LENGTH + "email", emailMin, emailMax, missing);
		range(LENGTH + "vehicle-plate", vehiclePlateMin, vehiclePlateMax, missing);
		if (observationMax.isPresent() && observationMax.get() < 1) {
			missing.add(LENGTH + "observation-max (debe ser al menos 1)");
		}

		if (!missing.isEmpty()) {
			LOGGER.error("Faltan o son invalidos parametros de validacion en el servidor de configuracion: {}", missing);
			if (!ParameterCatalog.isLoaded()) {
				ParameterCatalog.markLoaded(missing);
			}
			return;
		}

		ParameterCatalog.DOCUMENT_NUMBER_PATTERN = documentNumberPattern.orElseThrow();
		ParameterCatalog.DOCUMENT_NUMBER_MIN_LENGTH = documentNumberMin.orElseThrow();
		ParameterCatalog.DOCUMENT_NUMBER_MAX_LENGTH = documentNumberMax.orElseThrow();

		ParameterCatalog.FIRST_NAME_PATTERN = firstNamePattern.orElseThrow();
		ParameterCatalog.FIRST_NAME_MIN_LENGTH = firstNameMin.orElseThrow();
		ParameterCatalog.FIRST_NAME_MAX_LENGTH = firstNameMax.orElseThrow();

		ParameterCatalog.MIDDLE_NAME_PATTERN = middleNamePattern.orElseThrow();
		ParameterCatalog.MIDDLE_NAME_MIN_LENGTH = middleNameMin.orElseThrow();
		ParameterCatalog.MIDDLE_NAME_MAX_LENGTH = middleNameMax.orElseThrow();

		ParameterCatalog.LAST_NAME_PATTERN = lastNamePattern.orElseThrow();
		ParameterCatalog.LAST_NAME_MIN_LENGTH = lastNameMin.orElseThrow();
		ParameterCatalog.LAST_NAME_MAX_LENGTH = lastNameMax.orElseThrow();

		ParameterCatalog.SECOND_LAST_NAME_PATTERN = secondLastNamePattern.orElseThrow();
		ParameterCatalog.SECOND_LAST_NAME_MIN_LENGTH = secondLastNameMin.orElseThrow();
		ParameterCatalog.SECOND_LAST_NAME_MAX_LENGTH = secondLastNameMax.orElseThrow();

		ParameterCatalog.EMAIL_PATTERN = emailPattern.orElseThrow();
		ParameterCatalog.EMAIL_MIN_LENGTH = emailMin.orElseThrow();
		ParameterCatalog.EMAIL_MAX_LENGTH = emailMax.orElseThrow();

		ParameterCatalog.VEHICLE_PLATE_PATTERN = vehiclePlatePattern.orElseThrow();
		ParameterCatalog.VEHICLE_PLATE_MIN_LENGTH = vehiclePlateMin.orElseThrow();
		ParameterCatalog.VEHICLE_PLATE_MAX_LENGTH = vehiclePlateMax.orElseThrow();

		ParameterCatalog.OBSERVATION_MAX_LENGTH = observationMax.orElseThrow();
		ParameterCatalog.DOCUMENT_SIGNATURES.set(documentSignatures.orElseThrow());

		ParameterCatalog.markLoaded(List.of());
		LOGGER.info("Parametros de validacion cargados desde el servidor de configuracion");
	}

	private static Optional<String> pattern(final Environment environment, final String key,
			final List<String> missing) {
		final String value = environment.getProperty(key);
		if (ObjectHelper.isNull(value) || value.isBlank()) {
			missing.add(key);
			return Optional.empty();
		}
		try {
			Pattern.compile(value);
			return Optional.of(value);
		} catch (final PatternSyntaxException exception) {
			missing.add(key + " (expresion regular invalida)");
			return Optional.empty();
		}
	}

	private static Optional<Integer> number(final Environment environment, final String key,
			final List<String> missing) {
		final String value = environment.getProperty(key);
		if (ObjectHelper.isNull(value) || value.isBlank()) {
			missing.add(key);
			return Optional.empty();
		}
		try {
			return Optional.of(Integer.valueOf(value.trim()));
		} catch (final NumberFormatException exception) {
			missing.add(key + " (no es un numero entero)");
			return Optional.empty();
		}
	}

	private static void range(final String keyPrefix, final Optional<Integer> min, final Optional<Integer> max,
			final List<String> missing) {
		if (min.isEmpty() || max.isEmpty()) {
			return;
		}
		final int minimum = min.get();
		final int maximum = max.get();
		if (minimum < 0) {
			missing.add(keyPrefix + "-min (no puede ser negativo)");
		}
		if (maximum < 1 || maximum < minimum) {
			missing.add(keyPrefix + "-max (debe ser al menos 1 y no menor que el minimo)");
		}
	}

	private static Optional<Map<String, byte[]>> signatures(final Environment environment, final String key,
			final List<String> missing) {
		final String value = environment.getProperty(key);
		if (ObjectHelper.isNull(value) || value.isBlank()) {
			missing.add(key);
			return Optional.empty();
		}
		final Map<String, byte[]> signatures = new LinkedHashMap<>();
		for (final String entry : value.split(",")) {
			final String[] parts = entry.trim().split("=", 2);
			if (parts.length != 2 || parts[0].isBlank() || !parts[1].trim().matches("(?i)([0-9a-f]{2})+")) {
				missing.add(key + " (entrada invalida: '" + entry.trim() + "')");
				return Optional.empty();
			}
			signatures.put(parts[0].trim().toLowerCase(Locale.ROOT), hexToBytes(parts[1].trim()));
		}
		return Optional.of(Map.copyOf(signatures));
	}

	private static byte[] hexToBytes(final String hex) {
		final byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] = (byte) Integer.parseInt(hex.substring(2 * index, 2 * index + 2), 16);
		}
		return bytes;
	}

}
