package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;

/**
 * El visitante no puede tener ya una autorizacion activa en la misma propiedad que se cruce con el rango.
 * Datos: id del visitante, id de la propiedad, inicio, fin y {@link VisitAuthorizationRepository}.
 */
public final class VisitAuthorizationDoesNotOverlap implements Rule {

	private static final Rule instance = new VisitAuthorizationDoesNotOverlap();

	private VisitAuthorizationDoesNotOverlap() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 5) {
			throw VisitRuleErrors.incompleteParameters("VISIT_AUTHORIZATION_DOES_NOT_OVERLAP");
		}

		final var visitorId = (UUID) data[0];
		final var propertyId = (UUID) data[1];
		final var start = (Instant) data[2];
		final var end = (Instant) data[3];
		final var repository = (VisitAuthorizationRepository) data[4];

		if (repository.existsOverlappingAuthorization(visitorId, propertyId, start, end)) {
			throw VisitRuleErrors.business("VISIT_AUTHORIZATION_OVERLAPS");
		}
	}

}
