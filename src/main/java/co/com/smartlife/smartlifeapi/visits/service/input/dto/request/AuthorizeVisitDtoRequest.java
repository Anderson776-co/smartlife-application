package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorizeVisitDtoRequest(UUID propertyId, UUID visitReasonId, Instant startDate,
		Instant endDate, String observation, VisitorDtoRequest visitor, 
		@JsonIgnore @Schema(hidden = true) AttachedDocumentDtoRequest attachedDocument,
		@JsonIgnore @Schema(hidden = true) CurrentUser requester) {
	
	private static final CurrentUser ANONYMOUS =
			new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(), "", Set.of());
	
	public AuthorizeVisitDtoRequest {
		propertyId = UUIDHelper.getUUIDHelper().getDefault(propertyId);
		visitReasonId = UUIDHelper.getUUIDHelper().getDefault(visitReasonId);
		startDate = ObjectHelper.getDefault(startDate, Instant.EPOCH);
		endDate = ObjectHelper.getDefault(endDate, Instant.EPOCH);
		observation = TextHelper.getDefault(observation);
		visitor = ObjectHelper.getDefault(visitor, VisitorDtoRequest.empty());
		attachedDocument = ObjectHelper.getDefault(attachedDocument, AttachedDocumentDtoRequest.empty());
		requester = ObjectHelper.getDefault(requester, ANONYMOUS);
	}
	
	public AuthorizeVisitDtoRequest withAttachedDocument(final AttachedDocumentDtoRequest document) {
		return new AuthorizeVisitDtoRequest(propertyId, visitReasonId, startDate, endDate,
				observation, visitor, document, requester);
	}

}
