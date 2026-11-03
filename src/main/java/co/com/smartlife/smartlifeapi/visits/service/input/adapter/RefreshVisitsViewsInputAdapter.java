package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.RefreshVisitsViewsMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.RefreshVisitsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.RefreshVisitsViewsInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.RefreshVisitsViewsUseCase;

@Service
public class RefreshVisitsViewsInputAdapter implements RefreshVisitsViewsInputPort {

	private final RefreshVisitsViewsUseCase useCase;
	private final RefreshVisitsViewsMapperInput mapper;

	public RefreshVisitsViewsInputAdapter(final RefreshVisitsViewsUseCase useCase,
			final RefreshVisitsViewsMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final RefreshVisitsViewsDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
