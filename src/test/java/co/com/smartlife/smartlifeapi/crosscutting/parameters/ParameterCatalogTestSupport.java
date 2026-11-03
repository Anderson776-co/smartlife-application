package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.util.List;
import java.util.Map;

/**
 * Carga en el {@link ParameterCatalog} los mismos valores que se configuran en {@code smartlife-api.yaml}, para
 * que las pruebas unitarias no dependan del servidor de configuracion.
 */
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
		ParameterCatalog.DOCUMENT_SIGNATURES = Map.of(
				"application/pdf", new byte[] { '%', 'P', 'D', 'F' },
				"image/jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF },
				"image/png", new byte[] { (byte) 0x89, 'P', 'N', 'G' });

		ParameterCatalog.markLoaded(List.of());
	}

	/** Deja el catalogo como si no se hubiera cargado, para no afectar otras pruebas. */
	public static void unload() {
		ParameterCatalog.markLoaded(List.of("sin cargar"));
	}

}
