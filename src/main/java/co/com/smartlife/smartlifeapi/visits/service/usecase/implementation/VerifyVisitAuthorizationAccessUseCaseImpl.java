package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

/**
 * Politica de acceso para autorizar una visita (el RBAC lo resuelve Spring Security):
 * <ul>
 * <li>ABAC: el autorizador esta activo y pertenece a la urbanizacion de su sesion.</li>
 * <li>ReBAC: el autorizador tiene relacion con la propiedad, como propietario
 * (rol OWNER) o como residente (rol RESIDENT).</li>
 * </ul>
 */
@Service
public class VerifyVisitAuthorizationAccessUseCaseImpl implements VerifyVisitAuthorizationAccessUseCase {

	private final AuthorizatorRepository authorizatorRepository;
	private final PropertyVisitRepository propertyVisitRepository;
	private final VisitResidentRepository visitResidentRepository;

	public VerifyVisitAuthorizationAccessUseCaseImpl(final AuthorizatorRepository authorizatorRepository,
			final PropertyVisitRepository propertyVisitRepository,
			final VisitResidentRepository visitResidentRepository) {
		this.authorizatorRepository = authorizatorRepository;
		this.propertyVisitRepository = propertyVisitRepository;
		this.visitResidentRepository = visitResidentRepository;
	}

	@Override
	public Boolean execute(final VisitAuthorizationAccessDomain input) {
		final AuthorizatorDomain authorizator = input.authorizator();
		return isActiveInItsUrbanization(authorizator) && isRelatedToProperty(authorizator, input.propertyId());
	}

	private boolean isActiveInItsUrbanization(final AuthorizatorDomain authorizator) {
		return authorizatorRepository.findById(authorizator.id())
				.filter(registered -> Boolean.TRUE.equals(registered.getIsActive())
						&& Objects.equals(registered.getUrbanizationId(), authorizator.urbanizationId()))
				.isPresent();
	}

	private boolean isRelatedToProperty(final AuthorizatorDomain authorizator, final UUID propertyId) {
		return isOwnerOf(authorizator, propertyId) || isResidentOf(authorizator, propertyId);
	}

	private boolean isOwnerOf(final AuthorizatorDomain authorizator, final UUID propertyId) {
		return authorizator.isOwner() && propertyVisitRepository.isOwnedBy(propertyId, authorizator.id());
	}

	private boolean isResidentOf(final AuthorizatorDomain authorizator, final UUID propertyId) {
		return authorizator.isResident() && visitResidentRepository.residesIn(authorizator.id(), propertyId);
	}

}
