package co.com.smartlife.smartlifeapi.shared.domain.parameters;

/** Cambio ocurrido en el repositorio de parametros: el evento y la referencia de Git (rama) afectada. */
public record ParametersChangeDomain(ParametersChangeEventDomain event, String reference) {

	public ParametersChangeDomain {
		event = event == null ? ParametersChangeEventDomain.OTHER : event;
		reference = reference == null ? "" : reference.trim();
	}
}
