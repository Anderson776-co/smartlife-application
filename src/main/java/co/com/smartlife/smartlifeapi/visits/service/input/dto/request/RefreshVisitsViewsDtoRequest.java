package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/** Modulo de origen del cambio (el nombre de uno de los valores de VisitsViewSource). */
public record RefreshVisitsViewsDtoRequest(String source) {

	public RefreshVisitsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
