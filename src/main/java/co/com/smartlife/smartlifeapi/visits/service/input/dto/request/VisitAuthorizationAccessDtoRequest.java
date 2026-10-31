package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;

public record VisitAuthorizationAccessDtoRequest(CurrentUser requester, UUID propertyId) {

	public VisitAuthorizationAccessDtoRequest {
		propertyId = UUIDHelper.getUUIDHelper().getDefault(propertyId);
	}

}
