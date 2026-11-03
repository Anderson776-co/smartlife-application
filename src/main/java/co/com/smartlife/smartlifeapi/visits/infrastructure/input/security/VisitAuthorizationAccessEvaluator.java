package co.com.smartlife.smartlifeapi.visits.infrastructure.input.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.VerifyVisitAuthorizationAccessInputPort;

@Component(VisitAuthorizationAccessEvaluator.BEAN_NAME)
public class VisitAuthorizationAccessEvaluator {

	static final String BEAN_NAME = "visitAuthorizationAccessEvaluator";

	private final VerifyVisitAuthorizationAccessInputPort verifyAccess;

	public VisitAuthorizationAccessEvaluator(final VerifyVisitAuthorizationAccessInputPort verifyAccess) {
		this.verifyAccess = verifyAccess;
	}

	public boolean canAuthorize(final Authentication authentication, final UUID propertyId) {
		return !ObjectHelper.isNull(authentication)
				&& authentication.getPrincipal() instanceof CurrentUser requester
				&& Boolean.TRUE.equals(verifyAccess.execute(new VisitAuthorizationAccessDtoRequest(requester, propertyId)));
	}

}
