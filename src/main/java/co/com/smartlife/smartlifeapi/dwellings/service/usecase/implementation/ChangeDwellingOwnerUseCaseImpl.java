package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingDomain;
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingOwnerChangeDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.ChangeDwellingOwnerUseCase;
import java.util.Objects;

import co.com.smartlife.smartlifeapi.dwellings.domain.UserOwnerDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.DwellingRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.UserOwnerRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper.DwellingEventMapperUseCase;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper.DwellingMapperUseCase;

@Service
public class ChangeDwellingOwnerUseCaseImpl implements ChangeDwellingOwnerUseCase {

	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";

	private final DwellingRepository dwellingRepository;
	private final UserOwnerRepository userOwnerRepository;
	private final DwellingMapperUseCase dwellingMapper;
	private final DwellingEventMapperUseCase eventMapper;
	private final DomainEventPublisherOutputPort eventPublisher;

	public ChangeDwellingOwnerUseCaseImpl(final DwellingRepository dwellingRepository,
			final UserOwnerRepository userOwnerRepository, final DwellingMapperUseCase dwellingMapper,
			final DwellingEventMapperUseCase eventMapper, final DomainEventPublisherOutputPort eventPublisher) {
		this.dwellingRepository = dwellingRepository;
		this.userOwnerRepository = userOwnerRepository;
		this.dwellingMapper = dwellingMapper;
		this.eventMapper = eventMapper;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public void execute(final DwellingOwnerChangeDomain input) {
		final DwellingDomain dwelling = dwellingRepository.findById(input.dwellingId()).map(dwellingMapper::toDomain)
				.orElseThrow(() -> error("DWELLING_NOT_FOUND"));
		final UserOwnerDomain owner = userOwnerRepository.findById(input.ownerId()).map(dwellingMapper::toOwnerDomain)
				.orElseThrow(() -> error("DWELLING_OWNER_NOT_FOUND"));
		if (!Boolean.TRUE.equals(owner.active())) {
			throw error("DWELLING_OWNER_NOT_ACTIVE");
		}
		if (!Objects.equals(owner.urbanizationId(), dwelling.urbanization().id())) {
			throw error("DWELLING_OWNER_OTHER_URBANIZATION");
		}

		final DwellingDomain updated = dwelling.withOwner(owner);
		dwellingRepository.update(updated.id(), dwellingMapper.toEntity(updated));
		eventPublisher.publish(eventMapper.toEvent(updated));
	}

	private static SmartLifeException error(final String code) {
		return SmartLifeException.create(MessageCatalogProvider.getContent(USER_PREFIX + code),
				MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code));
	}

}
