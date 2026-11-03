package co.com.smartlife.smartlifeapi.residents.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper.RefreshResidentsViewsMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.RefreshResidentsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.input.port.RefreshResidentsViewsInputPort;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.RefreshResidentsViewsUseCase;

@Service
public class RefreshResidentsViewsInputAdapter implements RefreshResidentsViewsInputPort {

	private final RefreshResidentsViewsUseCase useCase;
	private final RefreshResidentsViewsMapperInput mapper;

	public RefreshResidentsViewsInputAdapter(final RefreshResidentsViewsUseCase useCase,
			final RefreshResidentsViewsMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final RefreshResidentsViewsDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
