package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification.AuthorizationSpecifications;

/**
 * Consulta las autorizaciones de un autorizador y las entrega con el mismo dominio con el que se autoriza. Los
 * filtros se arman con el patron Specification (ver {@link AuthorizationSpecifications}).
 */
@Service
public class ConsultVisitAuthorizationsUseCaseImpl implements ConsultVisitAuthorizationsUseCase {

	private static final String NOT_FOUND_CODE = "VISIT_AUTHORIZATION_NOT_FOUND";

	private final VisitAuthorizationRepository repository;
	private final Clock clock;

	public ConsultVisitAuthorizationsUseCaseImpl(final VisitAuthorizationRepository repository, final Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Override
	public List<VisitAuthorizationDomain> list(final UUID authorizatorId, final VisitAuthorizationStatus status,
			final Instant from, final Instant to, final UUID propertyId) {
		final Instant now = Instant.now(clock);
		final Specification<VisitAuthorizationEntity> specification = AuthorizationSpecifications.matching(status,
				from, to, propertyId, now);

		return repository.findByAuthorizatorId(authorizatorId).stream()
				.filter(specification::isSatisfiedBy)
				.map(authorization -> toDomain(authorization, now))
				.toList();
	}

	@Override
	public VisitAuthorizationDomain find(final UUID authorizationId, final UUID authorizatorId) {
		final Instant now = Instant.now(clock);
		return repository.findByIdAndAuthorizatorId(authorizationId, authorizatorId)
				.map(authorization -> toDomain(authorization, now))
				.orElseThrow(() -> SmartLifeException.create(
						MessageCatalogProvider.getContent("USER_MESSAGE_" + NOT_FOUND_CODE),
						MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_" + NOT_FOUND_CODE)));
	}

	/** No se devuelven los bytes del documento: ya estan en R2, solo la referencia. */
	private static VisitAuthorizationDomain toDomain(final VisitAuthorizationEntity authorization, final Instant now) {
		return new VisitAuthorizationDomain(authorization.getId(), authorization.getAuthorizationStartDate(),
				authorization.getAuthorizationEndDate(), authorization.getRegisterDate(),
				TextHelper.getDefault(authorization.getObservation()), authorization.getQrCode(),
				TextHelper.getDefault(authorization.getFileReference()), AttachedDocumentDomain.empty(),
				authorization.getProperty() == null ? null : authorization.getProperty().getId(),
				authorization.getVisitReason() == null ? null : authorization.getVisitReason().getId(),
				toAuthorizatorDomain(authorization.getAuthorizator()),
				authorization.getVisitor() == null ? null
						: AuthorizeVisitUseCaseImpl.toVisitorDomain(authorization.getVisitor()),
				false,
				authorization.getProperty() == null ? "" : authorization.getProperty().getLocation(),
				authorization.getVisitReason() == null ? "" : authorization.getVisitReason().getName(),
				statusOf(authorization, now));
	}

	private static AuthorizatorDomain toAuthorizatorDomain(final AuthorizatorEntity authorizator) {
		return authorizator == null ? null
				: new AuthorizatorDomain(authorizator.getId(), authorizator.getUrbanizationId(), Set.of());
	}

	/** Cada autorizacion tiene exactamente un estado: se busca cual de las Specification de estado cumple. */
	private static VisitAuthorizationStatus statusOf(final VisitAuthorizationEntity authorization, final Instant now) {
		for (final VisitAuthorizationStatus status : VisitAuthorizationStatus.values()) {
			if (AuthorizationSpecifications.withStatus(status, now).isSatisfiedBy(authorization)) {
				return status;
			}
		}
		return VisitAuthorizationStatus.CANCELLED;
	}
}
