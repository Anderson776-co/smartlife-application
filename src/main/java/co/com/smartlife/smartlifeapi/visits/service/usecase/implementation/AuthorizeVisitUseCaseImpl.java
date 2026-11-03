package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.TelemetryOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitAuthorizationMapperUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.RegisterVisitAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.ResolveVisitor;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitAuthorizationNotifier;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitDocumentStorage;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitorResolution;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateActiveAuthorizator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateAuthorizatorHasAccessToProperty;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateNoOverlappingAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidatePropertyVisitExistsById;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateUserAuthorizatorExistsById;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitDates;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitDocument;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitObservation;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitReasonExistsById;

@Service
public class AuthorizeVisitUseCaseImpl implements AuthorizeVisitUseCase {

	private static final String PATH_SEPARATOR = "/";
	static final String AUTHORIZATIONS_CREATED = "smartlife.visits.authorizations.created";
	static final String AUTHORIZATIONS_FAILED = "smartlife.visits.authorizations.failed";
	static final String NOTIFICATIONS_FAILED = "smartlife.visits.notifications.failed";
	static final String AUTHORIZATION_DURATION = "smartlife.visits.authorizations.duration";

	private final AuthorizatorRepository authorizatorRepository;
	private final PropertyVisitRepository propertyVisitRepository;
	private final VisitReasonRepository visitReasonRepository;
	private final VisitAuthorizationRepository visitAuthorizationRepository;
	private final VerifyVisitAuthorizationAccessUseCase verifyAccess;
	private final ResolveVisitor resolveVisitor;
	private final VisitDocumentStorage documentStorage;
	private final RegisterVisitAuthorization registerAuthorization;
	private final VisitAuthorizationNotifier notifier;
	private final TelemetryOutputPort telemetry;
	private final Clock clock;
	private final VisitAuthorizationMapperUseCase mapper;

	public AuthorizeVisitUseCaseImpl(final AuthorizatorRepository authorizatorRepository,
			final PropertyVisitRepository propertyVisitRepository, final VisitReasonRepository visitReasonRepository,
			final VisitAuthorizationRepository visitAuthorizationRepository,
			final VerifyVisitAuthorizationAccessUseCase verifyAccess, final ResolveVisitor resolveVisitor,
			final VisitDocumentStorage documentStorage, final RegisterVisitAuthorization registerAuthorization,
			final VisitAuthorizationNotifier notifier, final TelemetryOutputPort telemetry,
			final Clock clock, final VisitAuthorizationMapperUseCase mapper) {
		this.authorizatorRepository = authorizatorRepository;
		this.propertyVisitRepository = propertyVisitRepository;
		this.visitReasonRepository = visitReasonRepository;
		this.visitAuthorizationRepository = visitAuthorizationRepository;
		this.verifyAccess = verifyAccess;
		this.resolveVisitor = resolveVisitor;
		this.documentStorage = documentStorage;
		this.registerAuthorization = registerAuthorization;
		this.notifier = notifier;
		this.telemetry = telemetry;
		this.clock = clock;
		this.mapper = mapper;
	}

	@Override
	public VisitAuthorizationDomain execute(final VisitAuthorizationDomain input) {
		final long startedAt = System.nanoTime();
		try {
			final VisitAuthorizationDomain result = authorize(input);
			telemetry.incrementCounter(AUTHORIZATIONS_CREATED);
			return result;
		} catch (final RuntimeException exception) {
			telemetry.incrementCounter(AUTHORIZATIONS_FAILED);
			throw exception;
		} finally {
			telemetry.recordDuration(AUTHORIZATION_DURATION, Duration.ofNanos(System.nanoTime() - startedAt));
		}
	}

	private VisitAuthorizationDomain authorize(final VisitAuthorizationDomain input) {
		final Instant now = Instant.now(clock);
		ParameterCatalog.ensureLoaded();

		validateReferencesAndAccess(input);
		final AuthorizatorEntity authorizator = required(authorizatorRepository.findById(input.authorizator().id()));
		final PropertyVisitEntity property = required(propertyVisitRepository.findById(input.propertyId()));
		final VisitReasonEntity reason = required(visitReasonRepository.findById(input.visitReasonId()));

		validateBusinessRules(input, reason, now);
		final VisitorResolution visitor = resolveVisitor.resolve(input.visitor());
		validateNoOverlap(input, visitor);

		final UUID authorizationId = UUIDHelper.getUUIDHelper().generateNewUUID();
		final String fileReference = storeDocumentWhenRequired(authorizationId, reason, input.attachedDocument());
		final VisitAuthorizationDomain registration = new VisitAuthorizationDomain(authorizationId,
				input.authorizationStartDate(), input.authorizationEndDate(), now,
				TextHelper.getDefaultWithTrim(input.observation()), qrUrlFor(authorizationId),
				fileReference, AttachedDocumentDomain.empty(), input.propertyId(), input.visitReasonId(),
				input.authorizator(), input.visitor(), false);
		final VisitAuthorizationEntity authorization = mapper.toEntity(registration, property, authorizator,
				visitor.entity(), reason);

		register(authorization, visitor.isNew());

		final boolean notificationSent = notifier.notifyVisitor(authorization);
		if (!notificationSent) {
			telemetry.incrementCounter(NOTIFICATIONS_FAILED);
		}
		return mapper.toDomain(authorization, input.authorizator(), notificationSent);
	}

	private static String qrUrlFor(final UUID authorizationId) {
		final String base = ParameterCatalog.QR_BASE_URL.endsWith(PATH_SEPARATOR) ? ParameterCatalog.QR_BASE_URL
				: ParameterCatalog.QR_BASE_URL + PATH_SEPARATOR;
		return base + authorizationId;
	}

	private void validateReferencesAndAccess(final VisitAuthorizationDomain input) {
		ValidateUserAuthorizatorExistsById.executeValidation(input.authorizator().id(), authorizatorRepository);
		ValidateActiveAuthorizator.executeValidation(input.authorizator().id(), authorizatorRepository);
		ValidatePropertyVisitExistsById.executeValidation(input.propertyId(), propertyVisitRepository);
		ValidateVisitReasonExistsById.executeValidation(input.visitReasonId(), visitReasonRepository);
		ValidateAuthorizatorHasAccessToProperty.executeValidation(input.authorizator(), input.propertyId(), verifyAccess);
	}

	private void validateBusinessRules(final VisitAuthorizationDomain input, final VisitReasonEntity reason,
			final Instant now) {
		ValidateVisitDates.executeValidation(input.authorizationStartDate(), input.authorizationEndDate(), now);
		ValidateVisitObservation.executeValidation(input.observation());
		ValidateVisitDocument.executeValidation(reason, input.attachedDocument());
	}

	private void validateNoOverlap(final VisitAuthorizationDomain input, final VisitorResolution visitor) {
		if (!visitor.isNew()) {
			ValidateNoOverlappingAuthorization.executeValidation(visitor.entity().getId(), input.propertyId(),
					input.authorizationStartDate(), input.authorizationEndDate(), visitAuthorizationRepository);
		}
	}

	private String storeDocumentWhenRequired(final UUID authorizationId, final VisitReasonEntity reason,
			final AttachedDocumentDomain document) {
		return Boolean.TRUE.equals(reason.getRequiresDocument()) ? documentStorage.store(authorizationId, document) : TextHelper.getDefault();
	}

	private void register(final VisitAuthorizationEntity authorization, final boolean isNewVisitor) {
		try {
			registerAuthorization.register(authorization, isNewVisitor);
		} catch (final RuntimeException exception) {
			if (!TextHelper.isEmptyWithTrim(authorization.getFileReference())) {
				documentStorage.discard(authorization.getFileReference());
			}
			throw exception;
		}
	}

	private static <T> T required(final Optional<T> value) {
		return value.orElseThrow(() -> SmartLifeException.create(
				MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION"),
				"Un registro referenciado por la autorizacion dejo de existir durante la operacion"));
	}

}