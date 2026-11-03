package co.com.smartlife.smartlifeapi.residents.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record RefreshResidentsViewsDtoRequest(String source) {

	public RefreshResidentsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
