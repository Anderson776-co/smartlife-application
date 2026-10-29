package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.FileStorageOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.ResolveVisitor;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitAuthorizationNotifier;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateActiveAuthorizator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateAttachedDocument;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateAuthorizationData;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateNoOverlappingAuthorization;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidatePropertyBelongsToRequester;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateRequesterRole;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitReason;

@Service
public class AuthorizeVisitUseCaseImpl implements AuthorizeVisitUseCase{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeVisitUseCaseImpl.class);
	private static final String DOCUMENTS_PREFIX = "visits/authorizations/";
	private static final Map<String, String> EXTENSIONS =
			Map.of("application/pdf", ".pdf", "image/jpeg", ".jpg", "image/png", ".png");

	
	private final ValidateRequesterRole validateRequesterRole;
	private final ValidateActiveAuthorizator validateActiveAuthorizator;
	private final ValidatePropertyBelongsToRequester validatePropertyBelongsToRequester;
	private final ValidateAuthorizationData validateAuthorizationData;
	private final ValidateVisitReason validateVisitReason;
	private final ValidateAttachedDocument validateAttachedDocument;
	private final ResolveVisitor resolveVisitor;
	private final ValidateNoOverlappingAuthorization validateNoOverlappingAuthorization;
	private final VisitorRepository visitorRepository;
	private final VisitAuthorizationRepository visitAuthorizationRepository;
	private final FileStorageOutputPort fileStorage;
	//private final VisitParametersOutputPort parameters;
	private final VisitAuthorizationNotifier notifier;
	private final TransactionTemplate transactionTemplate;
	
	
	public AuthorizeVisitUseCaseImpl(final ValidateRequesterRole validateRequesterRole,
			final ValidateActiveAuthorizator validateActiveAuthorizator,
			final ValidatePropertyBelongsToRequester validatePropertyBelongsToRequester,
			final ValidateAuthorizationData validateAuthorizationData,
			final ValidateVisitReason validateVisitReason,
			final ValidateAttachedDocument validateAttachedDocument,
			final ResolveVisitor resolveVisitor,
			final ValidateNoOverlappingAuthorization validateNoOverlappingAuthorization,
			final VisitorRepository visitorRepository,
			final VisitAuthorizationRepository visitAuthorizationRepository,
			final FileStorageOutputPort fileStorage,
			final VisitAuthorizationNotifier notifier,
			final TransactionTemplate transactionTemplate) {
		this.validateRequesterRole = validateRequesterRole;
		this.validateActiveAuthorizator = validateActiveAuthorizator;
		this.validatePropertyBelongsToRequester = validatePropertyBelongsToRequester;
		this.validateAuthorizationData = validateAuthorizationData;
		this.validateVisitReason = validateVisitReason;
		this.validateAttachedDocument = validateAttachedDocument;
		this.resolveVisitor = resolveVisitor;
		this.validateNoOverlappingAuthorization = validateNoOverlappingAuthorization;
		this.visitorRepository = visitorRepository;
		this.visitAuthorizationRepository = visitAuthorizationRepository;
		this.fileStorage = fileStorage;
		//this.parameters = parameters;
		this.notifier = notifier;
		this.transactionTemplate = transactionTemplate;
	}
	
	@Override
	public VisitAuthorizationDomain execute(VisitAuthorizationDomain input) {
		// PENDIENTE (Anderson/Sara): implementar la autorizacion de la visita con las validaciones inyectadas.
		// PROVISIONAL: para que la aplicacion compile y arranque mientras tanto.
		throw new UnsupportedOperationException("El caso de uso de autorizar visita aun no esta implementado");
	}
	
}