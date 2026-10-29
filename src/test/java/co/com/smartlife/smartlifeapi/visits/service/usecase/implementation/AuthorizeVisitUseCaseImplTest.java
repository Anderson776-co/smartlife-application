package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.VisitParametersOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.RegisterVisitAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.ResolveVisitor;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitAuthorizationNotifier;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitDocumentStorage;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitorResolution;

class AuthorizeVisitUseCaseImplTest {

	private static final Instant NOW = Instant.parse("2026-10-29T15:00:00Z");
	private static final String QR_BASE_URL = "http://localhost:5173/visitas/autorizaciones/";
	private static final String DOCUMENT_KEY = "visits/authorizations/some-id/document.pdf";

	private final UUID authorizatorId = UUID.randomUUID();
	private final UUID urbanizationId = UUID.randomUUID();
	private final UUID propertyId = UUID.randomUUID();
	private final UUID familyReasonId = UUID.randomUUID();
	private final UUID technicalReasonId = UUID.randomUUID();

	private AuthorizatorRepository authorizatorRepository;
	private PropertyVisitRepository propertyVisitRepository;
	private VisitReasonRepository visitReasonRepository;
	private VisitAuthorizationRepository visitAuthorizationRepository;
	private VerifyVisitAuthorizationAccessUseCase verifyAccess;
	private ResolveVisitor resolveVisitor;
	private VisitDocumentStorage documentStorage;
	private RegisterVisitAuthorization registerAuthorization;
	private VisitAuthorizationNotifier notifier;
	private AuthorizeVisitUseCaseImpl useCase;

	private AuthorizatorDomain authorizator;
	private VisitorEntity newVisitor;

	@BeforeEach
	void setUp() {
		authorizatorRepository = mock(AuthorizatorRepository.class);
		propertyVisitRepository = mock(PropertyVisitRepository.class);
		visitReasonRepository = mock(VisitReasonRepository.class);
		visitAuthorizationRepository = mock(VisitAuthorizationRepository.class);
		verifyAccess = mock(VerifyVisitAuthorizationAccessUseCase.class);
		resolveVisitor = mock(ResolveVisitor.class);
		documentStorage = mock(VisitDocumentStorage.class);
		registerAuthorization = mock(RegisterVisitAuthorization.class);
		notifier = mock(VisitAuthorizationNotifier.class);
		final VisitParametersOutputPort parameters = mock(VisitParametersOutputPort.class);
		when(parameters.get()).thenReturn(new VisitParametersDTO(Duration.ofHours(24), Duration.ofDays(30),
				Duration.ofMinutes(10), 5L * 1024 * 1024, Set.of("application/pdf", "image/jpeg", "image/png"),
				QR_BASE_URL, ZoneId.of("UTC")));

		useCase = new AuthorizeVisitUseCaseImpl(authorizatorRepository, propertyVisitRepository,
				visitReasonRepository, visitAuthorizationRepository, verifyAccess, resolveVisitor, documentStorage,
				registerAuthorization, notifier, parameters, Clock.fixed(NOW, ZoneOffset.UTC));

		authorizator = new AuthorizatorDomain(authorizatorId, urbanizationId, Set.of("OWNER"));
		newVisitor = new VisitorEntity(UUID.randomUUID(), "Luis", "", "Torres", "", "2001", "luis@example.com", null);

		when(authorizatorRepository.findById(authorizatorId)).thenReturn(
				Optional.of(new AuthorizatorEntity(authorizatorId, "Ana Ruiz", true, urbanizationId)));
		when(propertyVisitRepository.findById(propertyId))
				.thenReturn(Optional.of(new PropertyVisitEntity(propertyId, "Torre 1 - 101", authorizatorId)));
		when(visitReasonRepository.findById(familyReasonId))
				.thenReturn(Optional.of(new VisitReasonEntity(familyReasonId, "Visita familiar", false)));
		when(visitReasonRepository.findById(technicalReasonId))
				.thenReturn(Optional.of(new VisitReasonEntity(technicalReasonId, "Servicio tecnico", true)));
		when(verifyAccess.execute(any(VisitAuthorizationAccessDomain.class))).thenReturn(true);
		when(resolveVisitor.resolve(any())).thenReturn(VisitorResolution.created(newVisitor));
		when(notifier.notifyVisitor(any())).thenReturn(true);
	}

	@Test
	void shouldRegisterTheAuthorizationAndNotifyTheVisitor() {
		final VisitAuthorizationDomain result = useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty()));

		assertNotNull(result.id());
		assertEquals(QR_BASE_URL + result.id(), result.qrCode());
		assertEquals(NOW, result.registerDate());
		assertEquals("", result.fileReference());
		assertTrue(result.notificationSent());
		assertEquals(newVisitor.getId(), result.visitor().id());

		final ArgumentCaptor<VisitAuthorizationEntity> saved = ArgumentCaptor.forClass(VisitAuthorizationEntity.class);
		verify(registerAuthorization).register(saved.capture(), eq(true));
		assertEquals(result.id(), saved.getValue().getId());
		assertTrue(saved.getValue().getIsActive());
		assertEquals(propertyId, saved.getValue().getProperty().getId());
		verify(documentStorage, never()).store(any(), any());
		verify(notifier).notifyVisitor(saved.getValue());
	}

	@Test
	void shouldStoreTheDocumentWhenTheReasonRequiresIt() {
		when(documentStorage.store(any(UUID.class), any())).thenReturn(DOCUMENT_KEY);

		final VisitAuthorizationDomain result = useCase.execute(request(technicalReasonId, pdf()));

		assertEquals(DOCUMENT_KEY, result.fileReference());
		assertFalse(result.attachedDocument().hasContent());
		final ArgumentCaptor<VisitAuthorizationEntity> saved = ArgumentCaptor.forClass(VisitAuthorizationEntity.class);
		verify(registerAuthorization).register(saved.capture(), anyBoolean());
		assertEquals(DOCUMENT_KEY, saved.getValue().getFileReference());
		verify(documentStorage).store(eq(result.id()), any());
	}

	@Test
	void shouldRejectWhenTheReasonRequiresADocumentAndNoneIsAttached() {
		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(technicalReasonId, AttachedDocumentDomain.empty())));

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		verify(documentStorage, never()).store(any(), any());
		verify(registerAuthorization, never()).register(any(), anyBoolean());
		verify(notifier, never()).notifyVisitor(any());
	}

	@Test
	void shouldDiscardTheStoredDocumentWhenPersistenceFails() {
		when(documentStorage.store(any(UUID.class), any())).thenReturn(DOCUMENT_KEY);
		doThrow(new IllegalStateException("db down")).when(registerAuthorization).register(any(), anyBoolean());

		assertThrows(IllegalStateException.class, () -> useCase.execute(request(technicalReasonId, pdf())));

		verify(documentStorage).discard(DOCUMENT_KEY);
		verify(notifier, never()).notifyVisitor(any());
	}

	@Test
	void shouldNotTouchTheStorageWhenPersistenceFailsWithoutDocument() {
		doThrow(new IllegalStateException("db down")).when(registerAuthorization).register(any(), anyBoolean());

		assertThrows(IllegalStateException.class,
				() -> useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty())));

		verify(documentStorage, never()).discard(any());
	}

	@Test
	void shouldKeepTheAuthorizationWhenTheNotificationFails() {
		when(notifier.notifyVisitor(any())).thenReturn(false);

		final VisitAuthorizationDomain result = useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty()));

		assertFalse(result.notificationSent());
		verify(registerAuthorization).register(any(), eq(true));
	}

	@Test
	void shouldRejectWhenTheAuthorizatorHasNoAccessToTheProperty() {
		when(verifyAccess.execute(any(VisitAuthorizationAccessDomain.class))).thenReturn(false);

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty())));

		assertEquals(SmartLifeException.Type.AUTHORIZATION, exception.getType());
		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	@Test
	void shouldRejectWhenTheAuthorizatorIsNotActive() {
		when(authorizatorRepository.findById(authorizatorId)).thenReturn(
				Optional.of(new AuthorizatorEntity(authorizatorId, "Ana Ruiz", false, urbanizationId)));

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty())));

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	@Test
	void shouldRejectWhenThePropertyDoesNotExist() {
		when(propertyVisitRepository.findById(propertyId)).thenReturn(Optional.empty());

		assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty())));

		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	@Test
	void shouldRejectWhenTheVisitReasonDoesNotExist() {
		assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(UUID.randomUUID(), AttachedDocumentDomain.empty())));

		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	@Test
	void shouldRejectAnExistingVisitorWithAnOverlappingAuthorization() {
		when(resolveVisitor.resolve(any())).thenReturn(VisitorResolution.existing(newVisitor));
		when(visitAuthorizationRepository.existsOverlappingAuthorization(eq(newVisitor.getId()), eq(propertyId),
				any(), any())).thenReturn(true);

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> useCase.execute(request(technicalReasonId, pdf())));

		assertEquals(SmartLifeException.Type.BUSINESS, exception.getType());
		verify(documentStorage, never()).store(any(), any());
		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	@Test
	void shouldNotPersistTheVisitorAgainWhenItAlreadyExists() {
		when(resolveVisitor.resolve(any())).thenReturn(VisitorResolution.existing(newVisitor));

		useCase.execute(request(familyReasonId, AttachedDocumentDomain.empty()));

		verify(registerAuthorization).register(any(), eq(false));
	}

	@Test
	void shouldRejectDatesInThePastWithoutPersisting() {
		final VisitAuthorizationDomain pastRequest = new VisitAuthorizationDomain(null, NOW.minus(Duration.ofHours(3)),
				NOW.minus(Duration.ofHours(1)), null, "", null, null, AttachedDocumentDomain.empty(), propertyId,
				familyReasonId, authorizator, visitor(), false);

		assertThrows(SmartLifeException.class, () -> useCase.execute(pastRequest));

		verify(registerAuthorization, never()).register(any(), anyBoolean());
	}

	private VisitAuthorizationDomain request(final UUID reasonId, final AttachedDocumentDomain document) {
		return new VisitAuthorizationDomain(null, NOW.plus(Duration.ofHours(1)), NOW.plus(Duration.ofHours(3)), null,
				"  Entrega de documentos  ", null, null, document, propertyId, reasonId, authorizator, visitor(), false);
	}

	private static VisitorDomain visitor() {
		return new VisitorDomain(null, "2001", "Luis", "", "Torres", "", "luis@example.com", "");
	}

	private static AttachedDocumentDomain pdf() {
		return new AttachedDocumentDomain("orden.pdf", "application/pdf",
				"%PDF-1.4 contenido".getBytes(StandardCharsets.US_ASCII));
	}

}
