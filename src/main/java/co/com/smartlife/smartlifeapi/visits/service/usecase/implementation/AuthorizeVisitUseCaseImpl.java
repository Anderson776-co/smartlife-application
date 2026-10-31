package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
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
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.RegisterVisitAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.ResolveVisitor;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitAuthorizationNotifier;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitDocumentStorage;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitorResolution;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.IsUUIDPresentRule;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateActiveAuthorizator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateAuthorizatorHasAccessToProperty;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateNoOverlappingAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidatePropertyVisitExistsById;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateUserAuthorizatorExistsById;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitDates;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitDocument;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitObservation;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitReasonExistsById;

/**
 * Autoriza la visita de una persona a una propiedad:
 * <ol>
 * <li>valida las reglas de negocio (referencias, acceso, fechas, documento y cruces);</li>
 * <li>guarda el documento en R2 solo si el motivo lo exige;</li>
 * <li>persiste visitante y autorizacion en una transaccion (si falla, retira el documento);</li>
 * <li>envia al visitante el correo con el QR, sin que un fallo del correo deshaga lo guardado.</li>
 * </ol>
 */
@Service
public class AuthorizeVisitUseCaseImpl implements AuthorizeVisitUseCase {

	private final AuthorizatorRepository authorizatorRepository;
	private final PropertyVisitRepository propertyVisitRepository;
	private final VisitReasonRepository visitReasonRepository;
	private final VisitAuthorizationRepository visitAuthorizationRepository;
	private final VerifyVisitAuthorizationAccessUseCase verifyAccess;
	private final ResolveVisitor resolveVisitor;
	private final VisitDocumentStorage documentStorage;
	private final RegisterVisitAuthorization registerAuthorization;
	private final VisitAuthorizationNotifier notifier;
	private final VisitParametersOutputPort parametersPort;
	private final Clock clock;

	public AuthorizeVisitUseCaseImpl(final AuthorizatorRepository authorizatorRepository,
			final PropertyVisitRepository propertyVisitRepository, final VisitReasonRepository visitReasonRepository,
			final VisitAuthorizationRepository visitAuthorizationRepository,
			final VerifyVisitAuthorizationAccessUseCase verifyAccess, final ResolveVisitor resolveVisitor,
			final VisitDocumentStorage documentStorage, final RegisterVisitAuthorization registerAuthorization,
			final VisitAuthorizationNotifier notifier, final VisitParametersOutputPort parametersPort,
			final Clock clock) {
		this.authorizatorRepository = authorizatorRepository;
		this.propertyVisitRepository = propertyVisitRepository;
		this.visitReasonRepository = visitReasonRepository;
		this.visitAuthorizationRepository = visitAuthorizationRepository;
		this.verifyAccess = verifyAccess;
		this.resolveVisitor = resolveVisitor;
		this.documentStorage = documentStorage;
		this.registerAuthorization = registerAuthorization;
		this.notifier = notifier;
		this.parametersPort = parametersPort;
		this.clock = clock;
	}

	@Override
	public VisitAuthorizationDomain execute(final VisitAuthorizationDomain input) {
		final Instant now = Instant.now(clock);
		final VisitParametersDTO parameters = parametersPort.get();

		validateReferencesAndAccess(input);
		final AuthorizatorEntity authorizator = required(authorizatorRepository.findById(input.authorizator().id()));
		final PropertyVisitEntity property = required(propertyVisitRepository.findById(input.propertyId()));
		final VisitReasonEntity reason = required(visitReasonRepository.findById(input.visitReasonId()));

		validateBusinessRules(input, reason, now, parameters);
		final VisitorResolution visitor = resolveVisitor.resolve(input.visitor());
		validateNoOverlap(input, visitor);

		final UUID authorizationId = UUIDHelper.getUUIDHelper().generateNewUUID();
		final String fileReference = storeDocumentWhenRequired(authorizationId, reason, input.attachedDocument());
		final VisitAuthorizationEntity authorization = new VisitAuthorizationEntity(authorizationId,
				input.authorizationStartDate(), input.authorizationEndDate(), now,
				TextHelper.getDefaultWithTrim(input.observation()), parameters.qrUrlFor(authorizationId),
				fileReference, Boolean.TRUE, property, authorizator, visitor.entity(), reason);

		register(authorization, visitor.isNew());

		final boolean notificationSent = notifier.notifyVisitor(authorization);
		return toDomain(authorization, input, notificationSent);
	}

	private void validateReferencesAndAccess(final VisitAuthorizationDomain input) {
		final UUID authorizatorId = input.authorizator().id();
		IsUUIDPresentRule.executeRule(input.propertyId());
		IsUUIDPresentRule.executeRule(input.visitReasonId());
		ValidateUserAuthorizatorExistsById.executeValidation(authorizatorId, authorizatorRepository);
		ValidateActiveAuthorizator.executeValidation(authorizatorId, authorizatorRepository);
		ValidatePropertyVisitExistsById.executeValidation(input.propertyId(), propertyVisitRepository);
		ValidateVisitReasonExistsById.executeValidation(input.visitReasonId(), visitReasonRepository);
		ValidateAuthorizatorHasAccessToProperty.executeValidation(input.authorizator(), input.propertyId(), verifyAccess);
	}

	private void validateBusinessRules(final VisitAuthorizationDomain input, final VisitReasonEntity reason,
			final Instant now, final VisitParametersDTO parameters) {
		ValidateVisitDates.executeValidation(input.authorizationStartDate(), input.authorizationEndDate(), now, parameters);
		ValidateVisitObservation.executeValidation(input.observation());
		ValidateVisitDocument.executeValidation(reason, input.attachedDocument(), parameters);
	}

	/** Un visitante nuevo no tiene autorizaciones previas, por eso solo se consulta cuando ya existia. */
	private void validateNoOverlap(final VisitAuthorizationDomain input, final VisitorResolution visitor) {
		if (!visitor.isNew()) {
			ValidateNoOverlappingAuthorization.executeValidation(visitor.entity().getId(), input.propertyId(),
					input.authorizationStartDate(), input.authorizationEndDate(), visitAuthorizationRepository);
		}
	}

	private String storeDocumentWhenRequired(final UUID authorizationId, final VisitReasonEntity reason,
			final AttachedDocumentDomain document) {
		return Boolean.TRUE.equals(reason.getRequiresDocument()) ? documentStorage.store(authorizationId, document) : null;
	}

	private void register(final VisitAuthorizationEntity authorization, final boolean isNewVisitor) {
		try {
			registerAuthorization.register(authorization, isNewVisitor);
		} catch (final RuntimeException exception) {
			if (authorization.getFileReference() != null) {
				documentStorage.discard(authorization.getFileReference());
			}
			throw exception;
		}
	}

	/** Las referencias ya se validaron; solo faltaria si alguien las elimina en medio de la operacion. */
	private static <T> T required(final Optional<T> value) {
		return value.orElseThrow(() -> SmartLifeException.create(
				MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION"),
				"Un registro referenciado por la autorizacion dejo de existir durante la operacion"));
	}

	/** No se devuelven los bytes del documento: ya quedaron en R2. */
	private static VisitAuthorizationDomain toDomain(final VisitAuthorizationEntity authorization,
			final VisitAuthorizationDomain input, final boolean notificationSent) {
		return new VisitAuthorizationDomain(authorization.getId(), authorization.getAuthorizationStartDate(),
				authorization.getAuthorizationEndDate(), authorization.getRegisterDate(),
				authorization.getObservation(), authorization.getQrCode(),
				TextHelper.getDefault(authorization.getFileReference()), AttachedDocumentDomain.empty(),
				authorization.getProperty().getId(), authorization.getVisitReason().getId(), input.authorizator(),
				toVisitorDomain(authorization.getVisitor()), notificationSent);
	}

	private static VisitorDomain toVisitorDomain(final VisitorEntity visitor) {
		return new VisitorDomain(visitor.getId(), visitor.getDocumentNumber(), visitor.getFirstName(),
				visitor.getMiddleName(), visitor.getLastName(), visitor.getSecondLastName(), visitor.getEmail(),
				TextHelper.getDefault(visitor.getVehiclePlate()));
	}

}
