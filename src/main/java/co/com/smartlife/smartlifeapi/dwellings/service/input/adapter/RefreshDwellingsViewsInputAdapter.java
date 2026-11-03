package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper.RefreshDwellingsViewsMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.RefreshDwellingsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.RefreshDwellingsViewsInputPort;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.RefreshDwellingsViewsUseCase;

@Service
public class RefreshDwellingsViewsInputAdapter implements RefreshDwellingsViewsInputPort {

	private final RefreshDwellingsViewsUseCase useCase;
	private final RefreshDwellingsViewsMapperInput mapper;

	public RefreshDwellingsViewsInputAdapter(final RefreshDwellingsViewsUseCase useCase,
			final RefreshDwellingsViewsMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final RefreshDwellingsViewsDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
