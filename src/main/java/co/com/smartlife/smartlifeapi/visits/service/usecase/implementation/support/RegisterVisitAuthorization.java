package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

@Component
public class RegisterVisitAuthorization {

	private final VisitorRepository visitorRepository;
	private final VisitAuthorizationRepository visitAuthorizationRepository;

	public RegisterVisitAuthorization(final VisitorRepository visitorRepository,
			final VisitAuthorizationRepository visitAuthorizationRepository) {
		this.visitorRepository = visitorRepository;
		this.visitAuthorizationRepository = visitAuthorizationRepository;
	}

	@Transactional
	public void register(final VisitAuthorizationEntity authorization, final boolean isNewVisitor) {
		if (isNewVisitor) {
			visitorRepository.create(authorization.getVisitor());
		}
		visitAuthorizationRepository.create(authorization);
	}

}
