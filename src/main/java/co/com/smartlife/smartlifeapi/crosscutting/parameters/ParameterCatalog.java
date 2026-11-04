package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class ParameterCatalog {

	private static final String NOT_LOADED = "los parametros aun no se han cargado";

	private static final AtomicReference<List<String>> MISSING_PARAMETERS = new AtomicReference<>(List.of(NOT_LOADED));

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

	public static volatile Duration MAX_AUTHORIZATION_DURATION;
	public static volatile Duration MAX_ANTICIPATION;
	public static volatile Duration START_TOLERANCE;
	public static volatile Long DOCUMENT_MAX_SIZE_IN_BYTES;
	public static Set<String> DOCUMENT_ALLOWED_TYPES;
	public static volatile String QR_BASE_URL;
	public static volatile ZoneId TIME_ZONE;
	public static volatile String QR_CONTENT_ID;
	public static volatile String QR_FILE_NAME;
	public static volatile String QR_CONTENT_TYPE;
	public static volatile String NOTIFICATION_DATE_PATTERN;
	public static volatile String DOCUMENT_STORAGE_KEY_FORMAT;
	public static volatile String DOCUMENT_DEFAULT_EXTENSION;
	public static Map<String, String> DOCUMENT_EXTENSIONS;


	public static final AtomicReference<Map<String, byte[]>> DOCUMENT_SIGNATURES = new AtomicReference<>(Map.of());

	public static void markLoaded(final List<String> missing) {
		MISSING_PARAMETERS.set(List.copyOf(missing));
	}

	public static boolean isLoaded() {
		return MISSING_PARAMETERS.get().isEmpty();
	}

	public static void ensureLoaded() {
		final List<String> missing = MISSING_PARAMETERS.get();
		if (!missing.isEmpty()) {
			throw new IllegalStateException(
					"Faltan o son invalidos parametros en el servidor de configuracion: " + missing);
		}
	}

}
