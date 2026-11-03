package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

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
		return VisitAuthorizationSummaryDtoMapper.toPage(useCase.list(data.currentUser().userId(), data.status(),
				data.from(), data.to(), data.propertyId()), data.page(), data.size());
	}
}
