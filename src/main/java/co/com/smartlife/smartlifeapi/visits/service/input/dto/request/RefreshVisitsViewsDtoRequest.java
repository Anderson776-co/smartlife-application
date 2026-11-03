package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record RefreshVisitsViewsDtoRequest(String source) {

	public RefreshVisitsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
