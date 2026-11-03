package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.users.domain.UserDomain;
import co.com.smartlife.smartlifeapi.users.domain.UserStatusChangeDomain;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.ChangeUserStatusUseCase;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper.UserEventMapperUseCase;
import co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper.UserMapperUseCase;

@Service
public class ChangeUserStatusUseCaseImpl implements ChangeUserStatusUseCase {

	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";

	private final UserRepository userRepository;
	private final UserMapperUseCase userMapper;
	private final UserEventMapperUseCase eventMapper;
	private final DomainEventPublisherOutputPort eventPublisher;

	public ChangeUserStatusUseCaseImpl(final UserRepository userRepository, final UserMapperUseCase userMapper,
			final UserEventMapperUseCase eventMapper, final DomainEventPublisherOutputPort eventPublisher) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.eventMapper = eventMapper;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public void execute(final UserStatusChangeDomain input) {
		if (ObjectHelper.isNull(input.active())) {
			throw error("USER_STATUS_REQUIRED");
		}
		final UserDomain current = userRepository.findById(input.userId()).map(userMapper::toDomain)
				.orElseThrow(() -> error("USER_NOT_FOUND"));

		final UserDomain updated = current.withActive(input.active());
		userRepository.update(updated.id(), userMapper.toEntity(updated));
		eventPublisher.publish(eventMapper.toEvent(updated));
	}

	private static SmartLifeException error(final String code) {
		return SmartLifeException.create(MessageCatalogProvider.getContent(USER_PREFIX + code),
				MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code));
	}

}
