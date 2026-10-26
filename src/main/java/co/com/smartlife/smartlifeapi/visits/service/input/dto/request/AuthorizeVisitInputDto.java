package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;

public record AuthorizeVisitInputDto(CurrentUser requester, AuthorizeVisitDtoRequest request,
		AttachedDocumentDtoRequest document) {

}
