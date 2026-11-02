package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.util.List;

/** Una pagina de autorizaciones; {@code page} empieza en 0. */
public record VisitAuthorizationPageDtoResponse(List<VisitAuthorizationSummaryDtoResponse> content, int page, int size,
		long totalElements, int totalPages) {

}
