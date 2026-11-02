package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.specification.AuthorizationSpecifications;

/**
 * Consulta las autorizaciones de un autorizador. Los filtros se arman con el patron Specification
 * (ver {@link AuthorizationSpecifications}) y se aplican sobre las autorizaciones de esa persona.
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
	public List<VisitAuthorizationSummaryDomain> list(final UUID authorizatorId,
			final VisitAuthorizationFilterDomain filter) {
		final Instant now = Instant.now(clock);
		final VisitAuthorizationFilterDomain criteria = filter == null ? VisitAuthorizationFilterDomain.none() : filter;
		final Specification<VisitAuthorizationEntity> specification = AuthorizationSpecifications
				.matching(criteria.status(), criteria.from(), criteria.to(), criteria.propertyId(), now);

		return repository.findByAuthorizatorId(authorizatorId).stream()
				.filter(specification::isSatisfiedBy)
				.map(authorization -> toSummary(authorization, now))
				.toList();
	}

	@Override
	public VisitAuthorizationPageDomain listPage(final UUID authorizatorId, final VisitAuthorizationFilterDomain filter,
			final int page, final int size) {
		return VisitAuthorizationPageDomain.of(list(authorizatorId, filter), page, size);
	}

	@Override
	public VisitAuthorizationSummaryDomain find(final UUID authorizationId, final UUID authorizatorId) {
		final Instant now = Instant.now(clock);
		return repository.findByIdAndAuthorizatorId(authorizationId, authorizatorId)
				.map(authorization -> toSummary(authorization, now))
				.orElseThrow(() -> SmartLifeException.create(
						MessageCatalogProvider.getContent("USER_MESSAGE_" + NOT_FOUND_CODE),
						MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_" + NOT_FOUND_CODE)));
	}

	private static VisitAuthorizationSummaryDomain toSummary(final VisitAuthorizationEntity authorization,
			final Instant now) {
		final VisitorEntity visitor = authorization.getVisitor();
		return new VisitAuthorizationSummaryDomain(authorization.getId(), authorization.getAuthorizationStartDate(),
				authorization.getAuthorizationEndDate(), authorization.getRegisterDate(),
				TextHelper.getDefault(authorization.getObservation()), authorization.getQrCode(),
				authorization.getProperty() == null ? "" : authorization.getProperty().getLocation(),
				authorization.getVisitReason() == null ? "" : authorization.getVisitReason().getName(),
				!TextHelper.getDefault(authorization.getFileReference()).isBlank(),
				visitor == null ? "" : visitor.getDocumentNumber(), fullName(visitor),
				visitor == null ? "" : visitor.getEmail(),
				visitor == null ? "" : TextHelper.getDefault(visitor.getVehiclePlate()),
				statusOf(authorization, now));
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

	private static String fullName(final VisitorEntity visitor) {
		if (visitor == null) {
			return "";
		}
		return Stream.of(visitor.getFirstName(), visitor.getMiddleName(), visitor.getLastName(),
				visitor.getSecondLastName()).filter(part -> part != null && !part.isBlank())
				.collect(Collectors.joining(" "));
	}
}
