package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryDtoMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ListVisitAuthorizationsInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

@Service
public class ListVisitAuthorizationsInputAdapter implements ListVisitAuthorizationsInputPort {

	private final ConsultVisitAuthorizationsUseCase useCase;

	public ListVisitAuthorizationsInputAdapter(final ConsultVisitAuthorizationsUseCase useCase) {
		this.useCase = useCase;
	}

	@Override
	public VisitAuthorizationPageDtoResponse execute(final ListVisitAuthorizationsDtoRequest data) {
		final VisitAuthorizationFilterDomain filter = new VisitAuthorizationFilterDomain(data.status(), data.from(),
				data.to(), data.propertyId());
		final VisitAuthorizationPageDomain page = useCase.listPage(data.currentUser().userId(), filter, data.page(),
				data.size());
		return new VisitAuthorizationPageDtoResponse(
				page.content().stream().map(VisitAuthorizationSummaryDtoMapper::toDto).toList(), page.page(),
				page.size(), page.totalElements(), page.totalPages());
	}
}
