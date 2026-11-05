package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.GetVisitAuthorizationInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

@Service
public class GetVisitAuthorizationInputAdapter implements GetVisitAuthorizationInputPort {

	private final ConsultVisitAuthorizationsUseCase useCase;
	private final VisitAuthorizationSummaryInputMapper responseMapper;

	public GetVisitAuthorizationInputAdapter(final ConsultVisitAuthorizationsUseCase useCase,
			final VisitAuthorizationSummaryInputMapper responseMapper) {
		this.useCase = useCase;
		this.responseMapper = responseMapper;
	}

	@Override
	public VisitAuthorizationSummaryDtoResponse execute(final GetVisitAuthorizationDtoRequest data) {
		return responseMapper.toDto(useCase.find(data.authorizationId(), data.currentUser().userId()));
	}
}
