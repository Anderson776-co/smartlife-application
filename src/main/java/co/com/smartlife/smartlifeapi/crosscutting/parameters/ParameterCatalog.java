package co.com.smartlife.smartlifeapi.crosscutting.parameters;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Catalogo de parametros de SmartLife. Los valores los carga
 * ParameterCatalogConfiguration al arrancar la aplicacion.
 */
public final class ParameterCatalog {

	private static final AtomicReference<VisitParameters> VISITS = new AtomicReference<>(VisitParameters.defaults());

	private ParameterCatalog() {
	}

	public static VisitParameters visits() {
		return VISITS.get();
	}

	public static void load(final VisitParameters visitParameters) {
		VISITS.set(Objects.requireNonNull(visitParameters, "Los parametros de visitas son obligatorio"));
	}
}