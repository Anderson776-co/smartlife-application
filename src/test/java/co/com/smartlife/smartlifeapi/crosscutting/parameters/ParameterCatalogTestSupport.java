package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ParameterCatalogTestSupport {

	public static final String NAME_PATTERN = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' ]+$";
	public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	private ParameterCatalogTestSupport() {
	}

	public static void load() {
		ParameterCatalog.DOCUMENT_NUMBER_PATTERN = "^[A-Za-z0-9-]+$";
		ParameterCatalog.DOCUMENT_NUMBER_MIN_LENGTH = 4;
		ParameterCatalog.DOCUMENT_NUMBER_MAX_LENGTH = 30;

		ParameterCatalog.FIRST_NAME_PATTERN = NAME_PATTERN;
		ParameterCatalog.FIRST_NAME_MIN_LENGTH = 1;
		ParameterCatalog.FIRST_NAME_MAX_LENGTH = 100;

		ParameterCatalog.MIDDLE_NAME_PATTERN = NAME_PATTERN;
		ParameterCatalog.MIDDLE_NAME_MIN_LENGTH = 1;
		ParameterCatalog.MIDDLE_NAME_MAX_LENGTH = 100;

		ParameterCatalog.LAST_NAME_PATTERN = NAME_PATTERN;
		ParameterCatalog.LAST_NAME_MIN_LENGTH = 1;
		ParameterCatalog.LAST_NAME_MAX_LENGTH = 100;

		ParameterCatalog.SECOND_LAST_NAME_PATTERN = NAME_PATTERN;
		ParameterCatalog.SECOND_LAST_NAME_MIN_LENGTH = 1;
		ParameterCatalog.SECOND_LAST_NAME_MAX_LENGTH = 100;

		ParameterCatalog.EMAIL_PATTERN = EMAIL_PATTERN;
		ParameterCatalog.EMAIL_MIN_LENGTH = 1;
		ParameterCatalog.EMAIL_MAX_LENGTH = 255;

		ParameterCatalog.VEHICLE_PLATE_PATTERN = "^[A-Za-z0-9-]+$";
		ParameterCatalog.VEHICLE_PLATE_MIN_LENGTH = 3;
		ParameterCatalog.VEHICLE_PLATE_MAX_LENGTH = 20;

		ParameterCatalog.OBSERVATION_MAX_LENGTH = 500;

		ParameterCatalog.MAX_AUTHORIZATION_DURATION = Duration.ofHours(24);
		ParameterCatalog.MAX_ANTICIPATION = Duration.ofDays(30);
		ParameterCatalog.START_TOLERANCE = Duration.ofMinutes(10);
		ParameterCatalog.DOCUMENT_MAX_SIZE_IN_BYTES = 5L * 1024 * 1024;
		ParameterCatalog.DOCUMENT_ALLOWED_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
		ParameterCatalog.QR_BASE_URL = "http://localhost:5173/visitas/autorizaciones/";
		ParameterCatalog.TIME_ZONE = ZoneId.of("UTC");
		ParameterCatalog.QR_CONTENT_ID = "visit-qr";
		ParameterCatalog.QR_FILE_NAME = "autorizacion-visita.png";
		ParameterCatalog.QR_CONTENT_TYPE = "image/png";
		ParameterCatalog.NOTIFICATION_DATE_PATTERN = "dd/MM/yyyy hh:mm a";
		ParameterCatalog.DOCUMENT_STORAGE_KEY_FORMAT = "visits/authorizations/%s/document.%s";
		ParameterCatalog.DOCUMENT_DEFAULT_EXTENSION = "bin";
		ParameterCatalog.DOCUMENT_EXTENSIONS = Map.of("application/pdf", "pdf", "image/jpeg", "jpg", "image/png", "png");
		ParameterCatalog.DOCUMENT_SIGNATURES.set(Map.of(
				"application/pdf", new byte[] { '%', 'P', 'D', 'F' },
				"image/jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF },
				"image/png", new byte[] { (byte) 0x89, 'P', 'N', 'G' }));

		ParameterCatalog.markLoaded(List.of());
	}

	public static void unload() {
		ParameterCatalog.markLoaded(List.of("sin cargar"));
	}

}
