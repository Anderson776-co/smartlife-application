package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.util.Objects;

/**
 * Catalogo de parametros de SmartLife. Los valores los carga
 * ParameterCatalogConfiguration al arrancar la aplicacion.
 */
public final class ParameterCatalog {

	private static volatile VisitParameters visits = VisitParameters.defaults();

	private ParameterCatalog() {
	}

	public static VisitParameters visits() {
		return visits;
	}

	public static void load(final VisitParameters visitParameters) {
		visits = Objects.requireNonNull(visitParameters, "Los parametros de visitas son obligatorios");
	}
}