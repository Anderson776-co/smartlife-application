package co.com.smartlife.smartlifeapi.users.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record RefreshUsersViewsDtoRequest(String source) {

	public RefreshUsersViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
