package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryDtoMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.GetVisitAuthorizationInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

@Service
public class GetVisitAuthorizationInputAdapter implements GetVisitAuthorizationInputPort {

	private final ConsultVisitAuthorizationsUseCase useCase;

	public GetVisitAuthorizationInputAdapter(final ConsultVisitAuthorizationsUseCase useCase) {
		this.useCase = useCase;
	}

	@Override
	public VisitAuthorizationSummaryDtoResponse execute(final GetVisitAuthorizationDtoRequest data) {
		return VisitAuthorizationSummaryDtoMapper
				.toDto(useCase.find(data.authorizationId(), data.currentUser().userId()));
	}
}
