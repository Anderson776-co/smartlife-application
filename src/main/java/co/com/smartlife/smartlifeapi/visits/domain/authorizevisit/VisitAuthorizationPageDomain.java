package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.List;

public record VisitAuthorizationPageDomain(List<VisitAuthorizationSummaryDomain> content, int page, int size,
		long totalElements, int totalPages) {

	public VisitAuthorizationPageDomain {
		content = List.copyOf(content);
	}
}
