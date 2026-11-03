package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.util.List;
import java.util.Map;

public final class ParameterCatalog {

	private static final String NOT_LOADED = "los parametros de validacion aun no se han cargado";

	private static volatile List<String> missingParameters = List.of(NOT_LOADED);

	private ParameterCatalog() {
	}

	public static volatile String DOCUMENT_NUMBER_PATTERN;
	public static volatile Integer DOCUMENT_NUMBER_MIN_LENGTH;
	public static volatile Integer DOCUMENT_NUMBER_MAX_LENGTH;

	public static volatile String FIRST_NAME_PATTERN;
	public static volatile Integer FIRST_NAME_MIN_LENGTH;
	public static volatile Integer FIRST_NAME_MAX_LENGTH;

	public static volatile String MIDDLE_NAME_PATTERN;
	public static volatile Integer MIDDLE_NAME_MIN_LENGTH;
	public static volatile Integer MIDDLE_NAME_MAX_LENGTH;

	public static volatile String LAST_NAME_PATTERN;
	public static volatile Integer LAST_NAME_MIN_LENGTH;
	public static volatile Integer LAST_NAME_MAX_LENGTH;

	public static volatile String SECOND_LAST_NAME_PATTERN;
	public static volatile Integer SECOND_LAST_NAME_MIN_LENGTH;
	public static volatile Integer SECOND_LAST_NAME_MAX_LENGTH;

	public static volatile String EMAIL_PATTERN;
	public static volatile Integer EMAIL_MIN_LENGTH;
	public static volatile Integer EMAIL_MAX_LENGTH;

	public static volatile String VEHICLE_PLATE_PATTERN;
	public static volatile Integer VEHICLE_PLATE_MIN_LENGTH;
	public static volatile Integer VEHICLE_PLATE_MAX_LENGTH;

	public static volatile Integer OBSERVATION_MAX_LENGTH;

	public static volatile Map<String, byte[]> DOCUMENT_SIGNATURES;

	public static void markLoaded(final List<String> missing) {
		missingParameters = List.copyOf(missing);
	}

	public static boolean isLoaded() {
		return missingParameters.isEmpty();
	}

	public static void ensureLoaded() {
		final List<String> missing = missingParameters;
		if (!missing.isEmpty()) {
			throw new IllegalStateException(
					"Faltan parametros de validacion en el servidor de configuracion: " + missing);
		}
	}

}
