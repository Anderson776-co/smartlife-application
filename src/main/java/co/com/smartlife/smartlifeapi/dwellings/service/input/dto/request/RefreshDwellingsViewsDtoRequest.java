package co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/** Modulo de origen del cambio (el nombre de uno de los valores de DwellingsViewSource). */
public record RefreshDwellingsViewsDtoRequest(String source) {

	public RefreshDwellingsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
