package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VisitAuthorizationSummaryDtoResponse(UUID authorizationId, Instant authorizationStartDate,
		Instant authorizationEndDate, String propertyLocation, String visitReason, boolean documentAttached,
		String visitorDocumentNumber, String visitorFullName, String visitorEmail, String vehiclePlate,
		@JsonProperty("isActive") Boolean isActive) {
}
