package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitAuthorizationSummaryMapperUseCase;

class ConsultVisitAuthorizationsUseCaseImplTest {

	private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");
	private static final Instant FROM = Instant.parse("2026-11-01T00:00:00Z");
	private static final Instant TO = Instant.parse("2026-11-30T00:00:00Z");
	private static final UUID AUTHORIZATOR = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID URBANIZATION = UUID.fromString("dddddddd-0000-0000-0000-000000000001");
	private static final UUID PROPERTY = UUID.fromString("cccccccc-0000-0000-0000-000000000001");

	private VisitAuthorizationRepository repository;
	private ConsultVisitAuthorizationsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		repository = mock(VisitAuthorizationRepository.class);
		useCase = new ConsultVisitAuthorizationsUseCaseImpl(repository,
				Mappers.getMapper(VisitAuthorizationSummaryMapperUseCase.class));
	}

	@AfterEach
	void resetMessageCatalog() {
		MessageCatalogProvider.init((code, locale) -> {
			throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
		});
	}

	private Criteria listAndCaptureCriteria(final VisitAuthorizationFilterDomain filter) {
		when(repository.count(any())).thenReturn(1L);
		when(repository.matching(any())).thenReturn(List.of(authorization(true, "")));
		useCase.list(filter);
		final ArgumentCaptor<Criteria> captor = ArgumentCaptor.forClass(Criteria.class);
		verify(repository).matching(captor.capture());
		return captor.getValue();
	}

	@Test
	void listWithoutOptionalFiltersShouldOnlyAskForTheAuthorizationsOfTheSessionUser() {
		final Criteria criteria = listAndCaptureCriteria(
				new VisitAuthorizationFilterDomain(AUTHORIZATOR, null, null, null, null, 0, 10));

		assertEquals(List.of(Filter.equalTo(VisitAuthorizationEntity.AUTHORIZATOR_ID, AUTHORIZATOR)),
				criteria.filters().values());
		assertEquals(Order.desc(VisitAuthorizationEntity.AUTHORIZATION_START_DATE), criteria.order());
		assertEquals(10, criteria.limit());
		assertEquals(0, criteria.offset());
	}

	@Test
	void listShouldAddEveryOptionalFilterThatIsInformed() {
		final Criteria criteria = listAndCaptureCriteria(
				new VisitAuthorizationFilterDomain(AUTHORIZATOR, true, FROM, TO, PROPERTY, 0, 10));

		assertEquals(List.of(
				Filter.equalTo(VisitAuthorizationEntity.AUTHORIZATOR_ID, AUTHORIZATOR),
				Filter.equalTo(VisitAuthorizationEntity.IS_ACTIVE, true),
				Filter.equalTo(VisitAuthorizationEntity.PROPERTY_ID, PROPERTY),
				Filter.greaterThan(VisitAuthorizationEntity.AUTHORIZATION_END_DATE, FROM),
				Filter.lessThan(VisitAuthorizationEntity.AUTHORIZATION_START_DATE, TO)),
				criteria.filters().values());
	}

	@Test
	void listShouldTranslateThePageIntoLimitAndOffsetAndReportTheTotals() {
		when(repository.count(any())).thenReturn(7L);
		when(repository.matching(any())).thenReturn(List.of(authorization(true, ""), authorization(false, "")));

		final VisitAuthorizationPageDomain page = useCase
				.list(new VisitAuthorizationFilterDomain(AUTHORIZATOR, null, null, null, null, 2, 3));

		final ArgumentCaptor<Criteria> captor = ArgumentCaptor.forClass(Criteria.class);
		verify(repository).matching(captor.capture());
		assertEquals(3, captor.getValue().limit());
		assertEquals(6, captor.getValue().offset());
		assertEquals(2, page.content().size());
		assertEquals(2, page.page());
		assertEquals(3, page.size());
		assertEquals(7, page.totalElements());
		assertEquals(3, page.totalPages());
	}

	@Test
	void listShouldNotQueryTheRowsWhenThePageIsBeyondTheLastOne() {
		when(repository.count(any())).thenReturn(2L);

		final VisitAuthorizationPageDomain page = useCase
				.list(new VisitAuthorizationFilterDomain(AUTHORIZATOR, null, null, null, null, 5, 10));

		verify(repository, never()).matching(any());
		assertTrue(page.content().isEmpty());
		assertEquals(2, page.totalElements());
		assertEquals(1, page.totalPages());
	}

	@Test
	void listShouldReturnAnEmptyPageWhenThereAreNoAuthorizations() {
		when(repository.count(any())).thenReturn(0L);

		final VisitAuthorizationPageDomain page = useCase
				.list(new VisitAuthorizationFilterDomain(AUTHORIZATOR, null, null, null, null, 0, 10));

		assertTrue(page.content().isEmpty());
		assertEquals(0, page.totalElements());
		assertEquals(0, page.totalPages());
	}

	@Test
	void theSummaryShouldCarryOnlyWhatTheListShowsAndTheIsActiveOfTheEntity() {
		when(repository.count(any())).thenReturn(2L);
		when(repository.matching(any())).thenReturn(List.of(
				authorization(true, "visits/authorizations/x/document.png"), authorization(false, "")));

		final List<VisitAuthorizationSummaryDomain> result = useCase
				.list(new VisitAuthorizationFilterDomain(AUTHORIZATOR, null, null, null, null, 0, 10)).content();

		final VisitAuthorizationSummaryDomain active = result.get(0);
		assertEquals("Luis Andres Torres Gomez", active.visitorFullName());
		assertEquals("luis@example.com", active.visitorEmail());
		assertEquals("2001", active.visitorDocumentNumber());
		assertEquals("ABC123", active.vehiclePlate());
		assertEquals("Apartamento 202 - Bloque B", active.propertyLocation());
		assertEquals("Servicio tecnico", active.visitReason());
		assertEquals(NOW, active.authorizationStartDate());
		assertEquals(NOW.plusSeconds(3600), active.authorizationEndDate());
		assertTrue(active.documentAttached());
		assertEquals(Boolean.TRUE, active.isActive());
		assertFalse(result.get(1).documentAttached());
		assertEquals(Boolean.FALSE, result.get(1).isActive());
	}

	@Test
	void findShouldReturnTheSummaryWhenTheAuthorizationBelongsToTheAuthorizator() {
		final VisitAuthorizationEntity own = authorization(true, "");
		when(repository.findByIdAndAuthorizatorId(own.getId(), AUTHORIZATOR)).thenReturn(Optional.of(own));

		final VisitAuthorizationSummaryDomain result = useCase.find(own.getId(), AUTHORIZATOR);

		assertEquals(own.getId(), result.id());
		assertEquals(Boolean.TRUE, result.isActive());
	}

	@Test
	void findShouldRejectWithTheCatalogMessagesWhenTheAuthorizationIsNotTheirs() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
		final UUID someoneElses = UUID.randomUUID();
		when(repository.findByIdAndAuthorizatorId(someoneElses, AUTHORIZATOR)).thenReturn(Optional.empty());

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.find(someoneElses, AUTHORIZATOR));

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		assertEquals("texto:USER_MESSAGE_VISIT_AUTHORIZATION_NOT_FOUND", exception.getUserMessage());
		assertEquals("texto:TECHNICAL_MESSAGE_VISIT_AUTHORIZATION_NOT_FOUND", exception.getTechnicalMessage());
	}

	private static VisitAuthorizationEntity authorization(final boolean active, final String fileReference) {
		final PropertyVisitEntity property = new PropertyVisitEntity(PROPERTY, "Apartamento 202 - Bloque B");
		final VisitorEntity visitor = new VisitorEntity(UUID.randomUUID(), "Luis", "Andres", "Torres", "Gomez", "2001",
				"luis@example.com", "ABC123");
		final VisitReasonEntity reason = new VisitReasonEntity(UUID.randomUUID(), "Servicio tecnico", true);
		final AuthorizatorEntity authorizator = new AuthorizatorEntity(AUTHORIZATOR, "Maria Perez", true, URBANIZATION);
		return new VisitAuthorizationEntity(UUID.randomUUID(), NOW, NOW.plusSeconds(3600), NOW.minusSeconds(86400),
				"Llega en moto", "https://smartlife.sarl/visitas/autorizaciones/x", fileReference, active, property,
				authorizator, visitor, reason);
	}
}
