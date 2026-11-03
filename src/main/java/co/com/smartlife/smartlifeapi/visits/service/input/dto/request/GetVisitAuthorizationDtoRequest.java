package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.util.Set;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;

public record GetVisitAuthorizationDtoRequest(UUID authorizationId, CurrentUser currentUser) {

	private static final CurrentUser ANONYMOUS = new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(), "",
			Set.of(), UUIDHelper.getUUIDHelper().getDefault());

	public GetVisitAuthorizationDtoRequest {
		authorizationId = UUIDHelper.getUUIDHelper().getDefault(authorizationId);
		currentUser = ObjectHelper.getDefault(currentUser, ANONYMOUS);
	}
}
