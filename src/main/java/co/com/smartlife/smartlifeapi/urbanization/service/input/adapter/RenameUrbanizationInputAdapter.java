package co.com.smartlife.smartlifeapi.urbanization.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.urbanization.service.input.adapter.mapper.UrbanizationRenameMapperInput;
import co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request.RenameUrbanizationDtoRequest;
import co.com.smartlife.smartlifeapi.urbanization.service.input.port.RenameUrbanizationInputPort;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.contract.RenameUrbanizationUseCase;

@Service
public class RenameUrbanizationInputAdapter implements RenameUrbanizationInputPort {

	private final RenameUrbanizationUseCase useCase;
	private final UrbanizationRenameMapperInput mapper;

	public RenameUrbanizationInputAdapter(final RenameUrbanizationUseCase useCase, final UrbanizationRenameMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final RenameUrbanizationDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
