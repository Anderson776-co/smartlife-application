package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filters;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitAuthorizationSummaryMapperUseCase;

@Service
public class ConsultVisitAuthorizationsUseCaseImpl implements ConsultVisitAuthorizationsUseCase {

	private static final String NOT_FOUND_CODE = "VISIT_AUTHORIZATION_NOT_FOUND";

	private final VisitAuthorizationRepository repository;
	private final VisitAuthorizationSummaryMapperUseCase mapper;

	public ConsultVisitAuthorizationsUseCaseImpl(final VisitAuthorizationRepository repository,
			final VisitAuthorizationSummaryMapperUseCase mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public VisitAuthorizationPageDomain list(final VisitAuthorizationFilterDomain filter) {
		final long offset = (long) filter.page() * filter.size();
		final Criteria criteria = new Criteria(new Filters(filtersOf(filter)),
				Order.desc(VisitAuthorizationEntity.AUTHORIZATION_START_DATE), filter.size(),
				(int) Math.min(offset, Integer.MAX_VALUE));

		final long total = repository.count(criteria);
		final List<VisitAuthorizationSummaryDomain> content = offset >= total ? List.of()
				: repository.matching(criteria).stream().map(mapper::toDomain).toList();
		final int totalPages = (int) ((total + filter.size() - 1) / filter.size());

		return new VisitAuthorizationPageDomain(content, filter.page(), filter.size(), total, totalPages);
	}

	@Override
	public VisitAuthorizationSummaryDomain find(final UUID authorizationId, final UUID authorizatorId) {
		return repository.findByIdAndAuthorizatorId(authorizationId, authorizatorId)
				.map(mapper::toDomain)
				.orElseThrow(() -> SmartLifeException.create(
						MessageCatalogProvider.getContent("USER_MESSAGE_" + NOT_FOUND_CODE),
						MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_" + NOT_FOUND_CODE)));
	}

	/** Solo las del residente en sesion; los demas filtros se agregan unicamente si vienen informados. */
	private static List<Filter> filtersOf(final VisitAuthorizationFilterDomain filter) {
		final List<Filter> filters = new ArrayList<>();
		filters.add(Filter.equalTo(VisitAuthorizationEntity.AUTHORIZATOR_ID, filter.authorizatorId()));
		if (filter.isActive() != null) {
			filters.add(Filter.equalTo(VisitAuthorizationEntity.IS_ACTIVE, filter.isActive()));
		}
		if (filter.propertyId() != null) {
			filters.add(Filter.equalTo(VisitAuthorizationEntity.PROPERTY_ID, filter.propertyId()));
		}
		// La autorizacion se cruza con [from, to] si empieza antes de "to" y termina despues de "from"
		if (filter.from() != null) {
			filters.add(Filter.greaterThan(VisitAuthorizationEntity.AUTHORIZATION_END_DATE, filter.from()));
		}
		if (filter.to() != null) {
			filters.add(Filter.lessThan(VisitAuthorizationEntity.AUTHORIZATION_START_DATE, filter.to()));
		}
		return filters;
	}
}
