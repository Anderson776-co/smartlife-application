package co.com.smartlife.smartlifeapi.visits.infrastructure.input.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.VerifyVisitAuthorizationAccessInputPort;

/**
 * Puente entre Spring Security y la politica de acceso del modulo de visitas.
 * Se invoca desde {@link CanAuthorizeVisit}.
 */
@Component(VisitAuthorizationAccessEvaluator.BEAN_NAME)
public class VisitAuthorizationAccessEvaluator {

	/** Nombre referenciado desde la expresion SpEL de {@link CanAuthorizeVisit}. */
	static final String BEAN_NAME = "visitAuthorizationAccessEvaluator";

	private final VerifyVisitAuthorizationAccessInputPort verifyAccess;

	public VisitAuthorizationAccessEvaluator(final VerifyVisitAuthorizationAccessInputPort verifyAccess) {
		this.verifyAccess = verifyAccess;
	}

	public boolean canAuthorize(final Authentication authentication, final UUID propertyId) {
		return authentication != null
				&& authentication.getPrincipal() instanceof CurrentUser requester
				&& Boolean.TRUE.equals(verifyAccess.execute(new VisitAuthorizationAccessDtoRequest(requester, propertyId)));
	}

}
