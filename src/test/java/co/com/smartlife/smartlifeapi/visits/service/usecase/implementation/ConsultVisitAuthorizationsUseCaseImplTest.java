package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class ConsultVisitAuthorizationsUseCaseImplTest {

    private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");
    private static final UUID AUTHORIZATOR = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
    private static final UUID PROPERTY_A = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
    private static final UUID PROPERTY_B = UUID.fromString("cccccccc-0000-0000-0000-000000000002");

    private VisitAuthorizationRepository repository;
    private ConsultVisitAuthorizationsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(VisitAuthorizationRepository.class);
        useCase = new ConsultVisitAuthorizationsUseCaseImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    /** El catalogo es estatico: se deja como "sin configurar" para no afectar otras pruebas. */
    @AfterEach
    void resetMessageCatalog() {
        MessageCatalogProvider.init((code, locale) -> {
            throw new IllegalStateException("Catalogo de mensajes no configurado en la prueba");
        });
    }

    private static Instant hours(final long fromNow) {
        return NOW.plusSeconds(fromNow * 3600);
    }

    private static VisitAuthorizationEntity authorization(final Instant start, final Instant end, final boolean active,
            final UUID propertyId, final String fileReference) {
        final PropertyVisitEntity property = new PropertyVisitEntity(propertyId, "Apartamento 202 - Bloque B", null);
        final VisitorEntity visitor = new VisitorEntity(UUID.randomUUID(), "Luis", "Andres", "Torres", "Gomez", "2001",
                "luis@example.com", "ABC123");
        final VisitReasonEntity reason = new VisitReasonEntity(UUID.randomUUID(), "Servicio tecnico", true);
        return new VisitAuthorizationEntity(UUID.randomUUID(), start, end, NOW.minusSeconds(86400), "Llega en moto",
                "https://smartlife.sarl/visitas/autorizaciones/x", fileReference, active, property, null, visitor,
                reason);
    }

    private static VisitAuthorizationEntity authorization(final Instant start, final Instant end, final boolean active,
            final UUID propertyId) {
        return authorization(start, end, active, propertyId, null);
    }

    @Test
    void listShouldReturnTheAuthorizationsWithTheirStatusKeepingTheRepositoryOrder() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR)).thenReturn(List.of(
                authorization(hours(1), hours(3), true, PROPERTY_A), authorization(hours(-1), hours(1), true, PROPERTY_A),
                authorization(hours(-3), hours(-1), true, PROPERTY_A), authorization(hours(-1), hours(1), false, PROPERTY_A)));

        final List<VisitAuthorizationSummaryDomain> result = useCase.list(AUTHORIZATOR,
                VisitAuthorizationFilterDomain.none());

        assertEquals(4, result.size());
        assertEquals(VisitAuthorizationStatus.UPCOMING, result.get(0).status());
        assertEquals(VisitAuthorizationStatus.CURRENT, result.get(1).status());
        assertEquals(VisitAuthorizationStatus.EXPIRED, result.get(2).status());
        assertEquals(VisitAuthorizationStatus.CANCELLED, result.get(3).status());
    }

    @Test
    void listShouldKeepOnlyTheAuthorizationsThatMatchTheStatusFilter() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR)).thenReturn(List.of(
                authorization(hours(1), hours(3), true, PROPERTY_A), authorization(hours(-1), hours(1), true, PROPERTY_A),
                authorization(hours(5), hours(6), true, PROPERTY_A)));

        final List<VisitAuthorizationSummaryDomain> result = useCase.list(AUTHORIZATOR,
                new VisitAuthorizationFilterDomain(VisitAuthorizationStatus.UPCOMING, null, null, null));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(summary -> summary.status() == VisitAuthorizationStatus.UPCOMING));
    }

    @Test
    void listShouldCombineThePropertyAndTheRangeFilters() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR)).thenReturn(List.of(
                authorization(hours(10), hours(12), true, PROPERTY_A), authorization(hours(10), hours(12), true, PROPERTY_B),
                authorization(hours(30), hours(32), true, PROPERTY_A)));

        final List<VisitAuthorizationSummaryDomain> result = useCase.list(AUTHORIZATOR,
                new VisitAuthorizationFilterDomain(null, hours(9), hours(13), PROPERTY_A));

        assertEquals(1, result.size());
        assertEquals(hours(10), result.get(0).authorizationStartDate());
    }

    @Test
    void listShouldTreatANullFilterAsNoFilter() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR))
                .thenReturn(List.of(authorization(hours(1), hours(3), true, PROPERTY_A)));

        assertEquals(1, useCase.list(AUTHORIZATOR, null).size());
    }

    @Test
    void listShouldOnlyAskForTheAuthorizatorsOwnAuthorizations() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR)).thenReturn(List.of());

        assertTrue(useCase.list(AUTHORIZATOR, null).isEmpty());

        verify(repository).findByAuthorizatorId(AUTHORIZATOR);
        verify(repository, never()).findAll();
    }

    @Test
    void theSummaryShouldCarryTheVisitorPropertyReasonAndDocumentData() {
        when(repository.findByAuthorizatorId(AUTHORIZATOR)).thenReturn(List.of(
                authorization(hours(1), hours(3), true, PROPERTY_A, "visits/authorizations/x/document.png"),
                authorization(hours(1), hours(3), true, PROPERTY_A)));

        final List<VisitAuthorizationSummaryDomain> result = useCase.list(AUTHORIZATOR, null);

        final VisitAuthorizationSummaryDomain withDocument = result.get(0);
        assertEquals("Luis Andres Torres Gomez", withDocument.visitorFullName());
        assertEquals("2001", withDocument.visitorDocumentNumber());
        assertEquals("luis@example.com", withDocument.visitorEmail());
        assertEquals("ABC123", withDocument.vehiclePlate());
        assertEquals("Apartamento 202 - Bloque B", withDocument.propertyLocation());
        assertEquals("Servicio tecnico", withDocument.visitReason());
        assertEquals("Llega en moto", withDocument.observation());
        assertTrue(withDocument.documentAttached());
        assertFalse(result.get(1).documentAttached());
    }

    @Test
    void findShouldReturnTheSummaryWhenTheAuthorizationBelongsToTheAuthorizator() {
        final VisitAuthorizationEntity own = authorization(hours(-1), hours(1), true, PROPERTY_A);
        when(repository.findByIdAndAuthorizatorId(own.getId(), AUTHORIZATOR)).thenReturn(Optional.of(own));

        final VisitAuthorizationSummaryDomain result = useCase.find(own.getId(), AUTHORIZATOR);

        assertEquals(own.getId(), result.id());
        assertEquals(VisitAuthorizationStatus.CURRENT, result.status());
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
}
