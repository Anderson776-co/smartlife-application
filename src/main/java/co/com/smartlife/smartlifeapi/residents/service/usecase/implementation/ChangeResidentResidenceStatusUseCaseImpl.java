package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceDomain;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceStatusChangeDomain;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.ChangeResidentResidenceStatusUseCase;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.ResidentDwellingRepository;
import co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper.ResidentResidenceEventMapperUseCase;
import co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper.ResidentResidenceMapperUseCase;

/**
 * Trabaja solo con objetos de dominio: lo que lee del repositorio lo pasa a dominio con un mapper, y para guardar
 * o publicar vuelve a pasar por un mapper (dominio a entidad, dominio a evento). El evento se publica dentro de la
 * transaccion y los listeners lo reciben cuando esta se confirma, es decir, cuando el cambio ya esta guardado.
 */
@Service
public class ChangeResidentResidenceStatusUseCaseImpl implements ChangeResidentResidenceStatusUseCase {

	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";

	private final ResidentDwellingRepository residentDwellingRepository;
	private final ResidentResidenceMapperUseCase relationMapper;
	private final ResidentResidenceEventMapperUseCase eventMapper;
	private final DomainEventPublisherOutputPort eventPublisher;

	public ChangeResidentResidenceStatusUseCaseImpl(final ResidentDwellingRepository residentDwellingRepository,
			final ResidentResidenceMapperUseCase relationMapper, final ResidentResidenceEventMapperUseCase eventMapper,
			final DomainEventPublisherOutputPort eventPublisher) {
		this.residentDwellingRepository = residentDwellingRepository;
		this.relationMapper = relationMapper;
		this.eventMapper = eventMapper;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public void execute(final ResidentResidenceStatusChangeDomain input) {
		if (input.active() == null) {
			throw error("RESIDENT_RESIDENCE_STATUS_REQUIRED");
		}
		final ResidentResidenceDomain current = residentDwellingRepository.findById(input.residentResidenceId())
				.map(relationMapper::toDomain).orElseThrow(() -> error("RESIDENT_RESIDENCE_NOT_FOUND"));

		final ResidentResidenceDomain updated = current.withActive(input.active());
		residentDwellingRepository.update(updated.id(), relationMapper.toEntity(updated));
		eventPublisher.publish(eventMapper.toEvent(updated));
	}

	private static SmartLifeException error(final String code) {
		return SmartLifeException.create(MessageCatalogProvider.getContent(USER_PREFIX + code),
				MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code));
	}

}
