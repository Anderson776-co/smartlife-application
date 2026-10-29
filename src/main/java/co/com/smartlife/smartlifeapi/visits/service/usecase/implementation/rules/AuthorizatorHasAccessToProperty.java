package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

/**
 * Reutiliza la politica de control de acceso (ABAC + ReBAC) tambien dentro del caso de uso, para que
 * la regla se cumpla aunque el caso de uso se invoque desde otra entrada distinta al controlador.
 * Datos: {@link AuthorizatorDomain}, id de la propiedad ({@link UUID}) y {@link VerifyVisitAuthorizationAccessUseCase}.
 */
public final class AuthorizatorHasAccessToProperty implements Rule {

	private static final Rule instance = new AuthorizatorHasAccessToProperty();

	private AuthorizatorHasAccessToProperty() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 3) {
			throw VisitRuleErrors.incompleteParameters("AUTHORIZATOR_HAS_ACCESS_TO_PROPERTY");
		}

		final var authorizator = (AuthorizatorDomain) data[0];
		final var propertyId = (UUID) data[1];
		final var verifyAccess = (VerifyVisitAuthorizationAccessUseCase) data[2];

		if (!Boolean.TRUE.equals(verifyAccess.execute(new VisitAuthorizationAccessDomain(authorizator, propertyId)))) {
			throw VisitRuleErrors.authorization("VISIT_AUTHORIZATOR_HAS_NO_ACCESS_TO_PROPERTY");
		}
	}

}
