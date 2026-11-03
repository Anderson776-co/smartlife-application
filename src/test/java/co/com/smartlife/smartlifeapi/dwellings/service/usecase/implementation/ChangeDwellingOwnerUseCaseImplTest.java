package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation;

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
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingOwnerChangeDomain;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.DwellingRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.UserOwnerRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.DwellingEntity;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UrbanizationDwellingEntity;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UserOwnerEntity;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper.DwellingEventMapperUseCase;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper.DwellingMapperUseCase;

class ChangeDwellingOwnerUseCaseImplTest {

	private static final UUID DWELLING_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
	private static final UUID OLD_OWNER = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001");
	private static final UUID NEW_OWNER = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID URBANIZATION = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	private DwellingRepository dwellingRepository;
	private UserOwnerRepository userOwnerRepository;
	private DomainEventPublisherOutputPort eventPublisher;
	private ChangeDwellingOwnerUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		dwellingRepository = mock(DwellingRepository.class);
		userOwnerRepository = mock(UserOwnerRepository.class);
		eventPublisher = mock(DomainEventPublisherOutputPort.class);
		useCase = new ChangeDwellingOwnerUseCaseImpl(dwellingRepository, userOwnerRepository,
				Mappers.getMapper(DwellingMapperUseCase.class), Mappers.getMapper(DwellingEventMapperUseCase.class),
				eventPublisher);
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
		when(dwellingRepository.findById(DWELLING_ID)).thenReturn(Optional.of(new DwellingEntity(DWELLING_ID, "101", "A",
				owner(OLD_OWNER, true, URBANIZATION), new UrbanizationDwellingEntity(URBANIZATION, "Conjunto"))));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private static UserOwnerEntity owner(final UUID id, final boolean active, final UUID urbanization) {
		return new UserOwnerEntity(id, "1002", "Maria Lopez", active, urbanization);
	}

	@Test
	void shouldSaveTheDwellingWithTheNewOwnerKeepingTheRestAndPublishTheEvent() {
		when(userOwnerRepository.findById(NEW_OWNER)).thenReturn(Optional.of(owner(NEW_OWNER, true, URBANIZATION)));

		useCase.execute(new DwellingOwnerChangeDomain(DWELLING_ID, NEW_OWNER));

		final ArgumentCaptor<DwellingEntity> captor = ArgumentCaptor.forClass(DwellingEntity.class);
		verify(dwellingRepository).update(eq(DWELLING_ID), captor.capture());
		final DwellingEntity saved = captor.getValue();
		assertEquals(NEW_OWNER, saved.getOwner().getId());
		assertEquals("Maria Lopez", saved.getOwner().getFullName());
		assertEquals(Boolean.TRUE, saved.getOwner().getIsActive());
		assertEquals("101", saved.getDwellingNumber());
		assertEquals("A", saved.getBlock());
		assertEquals(URBANIZATION, saved.getUrbanization().getId());
		verify(eventPublisher).publish(new DwellingUpdatedEvent(DWELLING_ID));
	}

	@Test
	void shouldRejectAnUnknownDwelling() {
		when(dwellingRepository.findById(DWELLING_ID)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new DwellingOwnerChangeDomain(DWELLING_ID, NEW_OWNER)));

		assertEquals("texto:USER_MESSAGE_DWELLING_NOT_FOUND", exception.getUserMessage());
		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectAnUnknownOwner() {
		when(userOwnerRepository.findById(NEW_OWNER)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new DwellingOwnerChangeDomain(DWELLING_ID, NEW_OWNER)));

		assertEquals("texto:USER_MESSAGE_DWELLING_OWNER_NOT_FOUND", exception.getUserMessage());
		verify(dwellingRepository, never()).update(any(), any());
	}

	@Test
	void shouldRejectAnInactiveOwner() {
		when(userOwnerRepository.findById(NEW_OWNER)).thenReturn(Optional.of(owner(NEW_OWNER, false, URBANIZATION)));

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new DwellingOwnerChangeDomain(DWELLING_ID, NEW_OWNER)));

		assertEquals("texto:USER_MESSAGE_DWELLING_OWNER_NOT_ACTIVE", exception.getUserMessage());
		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectAnOwnerFromAnotherUrbanization() {
		when(userOwnerRepository.findById(NEW_OWNER))
				.thenReturn(Optional.of(owner(NEW_OWNER, true, UUID.randomUUID())));

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new DwellingOwnerChangeDomain(DWELLING_ID, NEW_OWNER)));

		assertEquals("texto:USER_MESSAGE_DWELLING_OWNER_OTHER_URBANIZATION", exception.getUserMessage());
		verify(dwellingRepository, never()).update(any(), any());
	}
}
