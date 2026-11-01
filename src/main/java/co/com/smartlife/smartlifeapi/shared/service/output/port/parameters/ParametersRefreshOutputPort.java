package co.com.smartlife.smartlifeapi.shared.service.output.port.parameters;

import java.util.Set;

/** Recarga la configuracion desde el servidor de parametros. */
public interface ParametersRefreshOutputPort {

	/** Vuelve a leer la configuracion y devuelve las llaves cuyo valor cambio. */
	Set<String> refresh();
}
