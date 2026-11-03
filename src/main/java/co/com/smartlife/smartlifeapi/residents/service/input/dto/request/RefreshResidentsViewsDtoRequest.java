package co.com.smartlife.smartlifeapi.residents.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/** Modulo de origen del cambio (el nombre de uno de los valores de ResidentsViewSource). */
public record RefreshResidentsViewsDtoRequest(String source) {

	public RefreshResidentsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
