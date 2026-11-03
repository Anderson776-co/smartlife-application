package co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation;

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
import co.com.smartlife.smartlifeapi.urbanization.domain.UrbanizationRenameDomain;
import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.UrbanizationRepository;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper.UrbanizationEventMapperUseCase;
import co.com.smartlife.smartlifeapi.urbanization.service.usecase.implementation.mapper.UrbanizationMapperUseCase;

class RenameUrbanizationUseCaseImplTest {

	private static final UUID ID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	private UrbanizationRepository repository;
	private DomainEventPublisherOutputPort eventPublisher;
	private RenameUrbanizationUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		repository = mock(UrbanizationRepository.class);
		eventPublisher = mock(DomainEventPublisherOutputPort.class);
		useCase = new RenameUrbanizationUseCaseImpl(repository, Mappers.getMapper(UrbanizationMapperUseCase.class),
				Mappers.getMapper(UrbanizationEventMapperUseCase.class), eventPublisher);
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	@Test
	void shouldSaveTheNewNameAndPublishTheEvent() {
		when(repository.findById(ID)).thenReturn(Optional.of(new UrbanizationEntity(ID, "Conjunto viejo")));

		useCase.execute(new UrbanizationRenameDomain(ID, "Conjunto nuevo"));

		final ArgumentCaptor<UrbanizationEntity> captor = ArgumentCaptor.forClass(UrbanizationEntity.class);
		verify(repository).update(eq(ID), captor.capture());
		assertEquals(ID, captor.getValue().getId());
		assertEquals("Conjunto nuevo", captor.getValue().getName());
		verify(eventPublisher).publish(new UrbanizationUpdatedEvent(ID));
	}

	@Test
	void shouldRejectABlankName() {
		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new UrbanizationRenameDomain(ID, "   ")));

		assertEquals("texto:USER_MESSAGE_URBANIZATION_NAME_INVALID", exception.getUserMessage());
		verify(repository, never()).update(any(), any());
		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectANameLongerThanTheColumn() {
		assertThrows(SmartLifeException.class,
				() -> useCase.execute(new UrbanizationRenameDomain(ID, "x".repeat(151))));

		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectAnUnknownUrbanization() {
		when(repository.findById(ID)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new UrbanizationRenameDomain(ID, "Nuevo")));

		assertEquals("texto:USER_MESSAGE_URBANIZATION_NOT_FOUND", exception.getUserMessage());
		verify(repository, never()).update(any(), any());
		verify(eventPublisher, never()).publish(any());
	}
}
