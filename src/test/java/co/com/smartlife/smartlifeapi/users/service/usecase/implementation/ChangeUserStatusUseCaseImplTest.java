package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.users.domain.UserStatusChangeDomain;
import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UrbanizationUserEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;
import co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper.UserEventMapperUseCase;
import co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper.UserMapperUseCase;

class ChangeUserStatusUseCaseImplTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID ID_TYPE = UUID.fromString("dddddddd-0000-0000-0000-000000000001");
	private static final UUID URBANIZATION = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	private UserRepository userRepository;
	private DomainEventPublisherOutputPort eventPublisher;
	private ChangeUserStatusUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		eventPublisher = mock(DomainEventPublisherOutputPort.class);
		useCase = new ChangeUserStatusUseCaseImpl(userRepository, Mappers.getMapper(UserMapperUseCase.class),
				Mappers.getMapper(UserEventMapperUseCase.class), eventPublisher);
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private static UserEntity user(final boolean active) {
		return new UserEntity(USER_ID, "Maria", "Isabel", "Lopez", "Ruiz", "1002", "maria@example.com", true, false,
				active, "external", new IdTypeEntity(ID_TYPE, "Cedula"), new UrbanizationUserEntity(URBANIZATION, "Conjunto"));
	}

	@Test
	void shouldSaveTheUserWithTheNewStatusKeepingEveryOtherFieldAndPublishTheEvent() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(true)));

		useCase.execute(new UserStatusChangeDomain(USER_ID, false));

		final ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository).update(eq(USER_ID), captor.capture());
		final UserEntity saved = captor.getValue();
		assertEquals(Boolean.FALSE, saved.getIsActive());
		assertEquals("Maria", saved.getFirstName());
		assertEquals("Isabel", saved.getMiddleName());
		assertEquals("Lopez", saved.getLastName());
		assertEquals("Ruiz", saved.getSecondLastName());
		assertEquals("1002", saved.getDocumentNumber());
		assertEquals("maria@example.com", saved.getEmail());
		assertEquals(Boolean.TRUE, saved.getEmailConfirmed());
		assertEquals(Boolean.FALSE, saved.getEmailConfirmedDefaultValue());
		assertEquals("external", saved.getExternalId());
		assertEquals(ID_TYPE, saved.getIdType().getId());
		assertEquals(URBANIZATION, saved.getUrbanization().getId());
		verify(eventPublisher).publish(new UserUpdatedEvent(USER_ID));
	}

	@Test
	void shouldActivateAnInactiveUser() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(false)));

		useCase.execute(new UserStatusChangeDomain(USER_ID, true));

		final ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository).update(eq(USER_ID), captor.capture());
		assertEquals(Boolean.TRUE, captor.getValue().getIsActive());
	}

	@Test
	void shouldRejectAnUnknownUserWithoutSavingNorPublishing() {
		when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new UserStatusChangeDomain(USER_ID, true)));

		assertEquals("texto:USER_MESSAGE_USER_NOT_FOUND", exception.getUserMessage());
		verify(userRepository, never()).update(any(), any());
		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectARequestWithoutTheStatus() {
		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new UserStatusChangeDomain(USER_ID, null)));

		assertEquals("texto:USER_MESSAGE_USER_STATUS_REQUIRED", exception.getUserMessage());
		verify(eventPublisher, never()).publish(any());
	}
}
