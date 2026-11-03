package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation;

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
import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceStatusChangeDomain;
import co.com.smartlife.smartlifeapi.residents.event.ResidentResidenceUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.ResidentDwellingRepository;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidenceEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;
import co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper.ResidentResidenceEventMapperUseCase;
import co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper.ResidentResidenceMapperUseCase;

class ChangeResidentResidenceStatusUseCaseImplTest {

	private static final UUID RELATION_ID = UUID.fromString("99999999-0000-0000-0000-000000000001");
	private static final UUID RESIDENT_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID RESIDENCE_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final UUID URBANIZATION = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	private ResidentDwellingRepository repository;
	private DomainEventPublisherOutputPort eventPublisher;
	private ChangeResidentResidenceStatusUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		repository = mock(ResidentDwellingRepository.class);
		eventPublisher = mock(DomainEventPublisherOutputPort.class);
		useCase = new ChangeResidentResidenceStatusUseCaseImpl(repository,
				Mappers.getMapper(ResidentResidenceMapperUseCase.class),
				Mappers.getMapper(ResidentResidenceEventMapperUseCase.class), eventPublisher);
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
	}

	/** El catalogo es estatico: se deja como "sin configurar" para no afectar otras pruebas. */
	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private static ResidentResidenceEntity relation() {
		return new ResidentResidenceEntity(RELATION_ID,
				new ResidenceEntity(RESIDENCE_ID, "Bloque B, Vivienda 202", UUID.randomUUID(), URBANIZATION),
				new ResidentEntity(RESIDENT_ID, new UserResidentEntity(RESIDENT_ID, "1002", "Maria Lopez", true, URBANIZATION)),
				true);
	}

	@Test
	void shouldSaveTheRelationWithTheNewStatusKeepingTheRestAndPublishTheEvent() {
		when(repository.findById(RELATION_ID)).thenReturn(Optional.of(relation()));

		useCase.execute(new ResidentResidenceStatusChangeDomain(RELATION_ID, false));

		final ArgumentCaptor<ResidentResidenceEntity> captor = ArgumentCaptor.forClass(ResidentResidenceEntity.class);
		verify(repository).update(eq(RELATION_ID), captor.capture());
		final ResidentResidenceEntity saved = captor.getValue();
		assertEquals(Boolean.FALSE, saved.getIsActive());
		assertEquals(RESIDENT_ID, saved.getResident().getId());
		assertEquals(RESIDENCE_ID, saved.getResidence().getId());
		assertEquals("Bloque B, Vivienda 202", saved.getResidence().getLocation());
		verify(eventPublisher).publish(new ResidentResidenceUpdatedEvent(RELATION_ID, RESIDENT_ID));
	}

	@Test
	void shouldRejectAnUnknownRelation() {
		when(repository.findById(RELATION_ID)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new ResidentResidenceStatusChangeDomain(RELATION_ID, true)));

		assertEquals("texto:USER_MESSAGE_RESIDENT_RESIDENCE_NOT_FOUND", exception.getUserMessage());
		verify(repository, never()).update(any(), any());
		verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectARequestWithoutTheStatus() {
		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(new ResidentResidenceStatusChangeDomain(RELATION_ID, null)));

		assertEquals("texto:USER_MESSAGE_RESIDENT_RESIDENCE_STATUS_REQUIRED", exception.getUserMessage());
		verify(eventPublisher, never()).publish(any());
	}
}
