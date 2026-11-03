package co.com.smartlife.smartlifeapi.users.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/** Modulo de origen del cambio (el nombre de uno de los valores de UsersViewSource). */
public record RefreshUsersViewsDtoRequest(String source) {

	public RefreshUsersViewsDtoRequest {
		source = TextHelper.getDefaultWithTrim(source);
	}

}
