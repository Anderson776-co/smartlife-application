package co.com.smartlife.smartlifeapi.shared.domain.parameters;

/** Evento del repositorio de parametros que llega por webhook. */
public enum ParametersChangeEventDomain {
	PUSH,
	/** Cualquier otro evento (por ejemplo, el ping de verificacion): no hay nada que recargar. */
	OTHER
}
