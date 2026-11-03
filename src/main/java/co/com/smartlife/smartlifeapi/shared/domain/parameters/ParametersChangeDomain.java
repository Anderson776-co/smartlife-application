package co.com.smartlife.smartlifeapi.shared.domain.parameters;

public record ParametersChangeDomain(ParametersChangeEventDomain event, String reference) {

	public ParametersChangeDomain {
		reference = reference.trim();
	}
}
