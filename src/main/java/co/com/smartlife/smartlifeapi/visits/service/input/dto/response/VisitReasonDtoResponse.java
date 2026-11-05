package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.util.UUID;

public record VisitReasonDtoResponse(UUID id, String name, boolean requiresDocument) {

}
