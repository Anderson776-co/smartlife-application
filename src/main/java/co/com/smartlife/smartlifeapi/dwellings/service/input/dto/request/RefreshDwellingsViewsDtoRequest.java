package co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record RefreshDwellingsViewsDtoRequest(String source) {

	public RefreshDwellingsViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
