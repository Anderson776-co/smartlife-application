package co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationDomain;
import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationRenameDomain;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.contract.RenameUrbanizationUseCase;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.UrbanizationRepository;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper.UrbanizationEventMapperUseCase;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper.UrbanizationMapperUseCase;

@Service
public class RenameUrbanizationUseCaseImpl implements RenameUrbanizationUseCase {

	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";

	private static final int MAX_NAME_LENGTH = 150;

	private final UrbanizationRepository urbanizationRepository;
	private final UrbanizationMapperUseCase urbanizationMapper;
	private final UrbanizationEventMapperUseCase eventMapper;
	private final DomainEventPublisherOutputPort eventPublisher;

	public RenameUrbanizationUseCaseImpl(final UrbanizationRepository urbanizationRepository,
			final UrbanizationMapperUseCase urbanizationMapper, final UrbanizationEventMapperUseCase eventMapper,
			final DomainEventPublisherOutputPort eventPublisher) {
		this.urbanizationRepository = urbanizationRepository;
		this.urbanizationMapper = urbanizationMapper;
		this.eventMapper = eventMapper;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public void execute(final UrbanizationRenameDomain input) {
		if (!TextHelper.lengthIsValidWithTrim(input.name(), 1, MAX_NAME_LENGTH)) {
			throw error("URBANIZATION_NAME_INVALID");
		}
		final UrbanizationDomain current = urbanizationRepository.findById(input.urbanizationId())
				.map(urbanizationMapper::toDomain).orElseThrow(() -> error("URBANIZATION_NOT_FOUND"));

		final UrbanizationDomain updated = current.withName(input.name());
		urbanizationRepository.update(updated.id(), urbanizationMapper.toEntity(updated));
		eventPublisher.publish(eventMapper.toEvent(updated));
	}

	private static SmartLifeException error(final String code) {
		return SmartLifeException.create(MessageCatalogProvider.getContent(USER_PREFIX + code),
				MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code));
	}

}
