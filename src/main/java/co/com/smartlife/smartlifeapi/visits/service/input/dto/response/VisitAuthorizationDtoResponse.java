package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.util.UUID;

public record VisitAuthorizationDtoResponse(UUID authorizationId, String qrUrl, boolean notificationSent) {

}
