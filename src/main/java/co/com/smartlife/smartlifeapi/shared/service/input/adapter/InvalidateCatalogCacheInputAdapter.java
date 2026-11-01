package co.com.smartlife.smartlifeapi.shared.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.service.input.adapter.mapper.CatalogWebhookMapperInput;
import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;
import co.com.smartlife.smartlifeapi.shared.service.input.port.InvalidateCatalogCacheInputPort;
import co.com.smartlife.smartlifeapi.shared.service.usecase.contract.InvalidateCatalogCacheUseCase;

@Service
public class InvalidateCatalogCacheInputAdapter implements InvalidateCatalogCacheInputPort {

	private final InvalidateCatalogCacheUseCase useCase;
	private final CatalogWebhookMapperInput mapper;

	public InvalidateCatalogCacheInputAdapter(final InvalidateCatalogCacheUseCase useCase,
			final CatalogWebhookMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final CatalogWebhookDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}
}
