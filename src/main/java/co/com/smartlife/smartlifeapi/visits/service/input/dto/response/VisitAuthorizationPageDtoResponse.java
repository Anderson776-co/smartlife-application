package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.util.List;

public record VisitAuthorizationPageDtoResponse(List<VisitAuthorizationSummaryDtoResponse> content, int page, int size,
		long totalElements, int totalPages) {

}
