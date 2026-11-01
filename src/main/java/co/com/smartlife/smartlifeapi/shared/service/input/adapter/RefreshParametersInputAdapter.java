package co.com.smartlife.smartlifeapi.shared.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper.ConfigWebhookMapperInput;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.port.RefreshParametersInputPort;
import co.com.smartlife.smartlifeapi.shared.service.usecase.contract.RefreshParametersUseCase;

@Service
public class RefreshParametersInputAdapter implements RefreshParametersInputPort {

	private final RefreshParametersUseCase useCase;
	private final ConfigWebhookMapperInput mapper;

	public RefreshParametersInputAdapter(final RefreshParametersUseCase useCase,
			final ConfigWebhookMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final ConfigWebhookDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}
}
