package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.time.Instant;
import java.util.UUID;

public record AuthorizeVisitDtoRequest(UUID propertyId, UUID visitReasonId, Instant startDate,
		Instant endDate, String observation, VisitorDtoRequest visitor) {

}
