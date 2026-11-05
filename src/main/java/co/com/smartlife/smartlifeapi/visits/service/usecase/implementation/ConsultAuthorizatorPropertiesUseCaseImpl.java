package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultAuthorizatorPropertiesUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.PropertyVisitMapperUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateActiveAuthorizator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateUserAuthorizatorExistsById;


@Service
public class ConsultAuthorizatorPropertiesUseCaseImpl implements ConsultAuthorizatorPropertiesUseCase {

	private final AuthorizatorRepository authorizatorRepository;
	private final VisitResidentRepository visitResidentRepository;
	private final PropertyVisitRepository propertyVisitRepository;
	private final PropertyVisitMapperUseCase mapperUseCase;
	
	
	public ConsultAuthorizatorPropertiesUseCaseImpl(final AuthorizatorRepository authorizatorRepository,
			final VisitResidentRepository visitResidentRepository, final PropertyVisitRepository propertyVisitRepository,
			final PropertyVisitMapperUseCase propertyVisitMapperUseCase) {
		this.authorizatorRepository = authorizatorRepository;
		this.visitResidentRepository = visitResidentRepository;
		this.propertyVisitRepository = propertyVisitRepository;
		this.mapperUseCase = propertyVisitMapperUseCase;
	}

	@Override
	public Optional<PropertyVisitDomain> list(
	        final AuthorizatorDomain authorizator) {

	    ValidateUserAuthorizatorExistsById.executeValidation(authorizator.id(), authorizatorRepository);

	    ValidateActiveAuthorizator.executeValidation(authorizator.id(), authorizatorRepository);

	    final Optional<UUID> propertyId = visitResidentRepository.findById(authorizator.id())
	                    .map(visitResident ->
	                            visitResident.getProperty().getId());

	    return propertyId.flatMap(id ->
	           	propertyVisitRepository.matching(Criteria.of(Filter.equalTo(PropertyVisitEntity.ID,id)))
	            .stream()
	            .findFirst())
	            .map(mapperUseCase::toDomain);
	}

}
