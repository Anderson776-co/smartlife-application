package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationFilterMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ListVisitAuthorizationsInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

@Service
public class ListVisitAuthorizationsInputAdapter implements ListVisitAuthorizationsInputPort {

	private final ConsultVisitAuthorizationsUseCase useCase;
	private final VisitAuthorizationFilterMapperInput filterMapper;
	private final VisitAuthorizationSummaryInputMapper responseMapper;

	public ListVisitAuthorizationsInputAdapter(final ConsultVisitAuthorizationsUseCase useCase,
			final VisitAuthorizationFilterMapperInput filterMapper,
			final VisitAuthorizationSummaryInputMapper responseMapper) {
		this.useCase = useCase;
		this.filterMapper = filterMapper;
		this.responseMapper = responseMapper;
	}

	@Override
	public VisitAuthorizationPageDtoResponse execute(final ListVisitAuthorizationsDtoRequest data) {
		return responseMapper.toPageDto(useCase.list(filterMapper.toDomain(data)));
	}
}
