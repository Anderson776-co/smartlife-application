package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.Objects;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

/** Solo puede autorizar visitas sobre una vivienda quien es residente de ella en el módulo de visitas. */
@Service
public class VerifyVisitAuthorizationAccessUseCaseImpl implements VerifyVisitAuthorizationAccessUseCase {

	private final AuthorizatorRepository authorizatorRepository;
	private final VisitResidentRepository visitResidentRepository;

	public VerifyVisitAuthorizationAccessUseCaseImpl(final AuthorizatorRepository authorizatorRepository,
			final VisitResidentRepository visitResidentRepository) {
		this.authorizatorRepository = authorizatorRepository;
		this.visitResidentRepository = visitResidentRepository;
	}

	@Override
	public Boolean execute(final VisitAuthorizationAccessDomain input) {
		final AuthorizatorDomain authorizator = input.authorizator();
		return authorizator.isResident() && isActiveInItsUrbanization(authorizator)
				&& visitResidentRepository.residesIn(authorizator.id(), input.propertyId());
	}

	private boolean isActiveInItsUrbanization(final AuthorizatorDomain authorizator) {
		return authorizatorRepository.findById(authorizator.id())
				.filter(registered -> Boolean.TRUE.equals(registered.getIsActive())
						&& Objects.equals(registered.getUrbanizationId(), authorizator.urbanizationId()))
				.isPresent();
	}

}
