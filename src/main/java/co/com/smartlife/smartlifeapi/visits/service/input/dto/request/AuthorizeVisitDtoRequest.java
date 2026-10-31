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

public record AuthorizeVisitDtoRequest(UUID propertyId, UUID visitReasonId, Instant authorizationStartDate,
		Instant authorizationEndDate, String observation, VisitorDtoRequest visitor, 
		@JsonIgnore @Schema(hidden = true) AttachedDocumentDtoRequest attachedDocument,
		@JsonIgnore @Schema(hidden = true) CurrentUser authorizator) {
	
	private static final CurrentUser ANONYMOUS =
			new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(),"", Set.of(), UUIDHelper.getUUIDHelper().getDefault());
	
	public AuthorizeVisitDtoRequest {
		propertyId = UUIDHelper.getUUIDHelper().getDefault(propertyId);
		visitReasonId = UUIDHelper.getUUIDHelper().getDefault(visitReasonId);
		authorizationStartDate = ObjectHelper.getDefault(authorizationStartDate, Instant.EPOCH);
		authorizationEndDate = ObjectHelper.getDefault(authorizationEndDate, Instant.EPOCH);
		observation = TextHelper.getDefault(observation);
		visitor = ObjectHelper.getDefault(visitor, VisitorDtoRequest.empty());
		attachedDocument = ObjectHelper.getDefault(attachedDocument, AttachedDocumentDtoRequest.empty());
		authorizator = ObjectHelper.getDefault(authorizator, ANONYMOUS);
	}
	
	public AuthorizeVisitDtoRequest withContext(final AttachedDocumentDtoRequest document, final CurrentUser currentUser) {
		return new AuthorizeVisitDtoRequest(propertyId, visitReasonId, authorizationStartDate, authorizationEndDate,
				observation, visitor, document, currentUser);
	}

}
