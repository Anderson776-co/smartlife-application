package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.service.output.port.TelemetryOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

@Component
public class RegisterVisitAuthorization {

	public static final String NOTIFICATIONS_FAILED = "smartlife.visits.notifications.failed";
	private static final String USER_MESSAGE_CODE = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";
	private static final String TECHNICAL_MESSAGE = "No se pudo enviar el correo al visitante, por eso la autorizacion no se guardo";

	private final VisitorRepository visitorRepository;
	private final VisitAuthorizationRepository visitAuthorizationRepository;
	private final VisitAuthorizationNotifier notifier;
	private final TelemetryOutputPort telemetry;

	public RegisterVisitAuthorization(final VisitorRepository visitorRepository,
			final VisitAuthorizationRepository visitAuthorizationRepository,
			final VisitAuthorizationNotifier notifier, final TelemetryOutputPort telemetry) {
		this.visitorRepository = visitorRepository;
		this.visitAuthorizationRepository = visitAuthorizationRepository;
		this.notifier = notifier;
		this.telemetry = telemetry;
	}

	@Transactional
	public void register(final VisitAuthorizationEntity authorization, final boolean isNewVisitor) {
		if (isNewVisitor) {
			visitorRepository.create(authorization.getVisitor());
		}
		visitAuthorizationRepository.create(authorization);

		if (!notifier.notifyVisitor(authorization)) {
			telemetry.incrementCounter(NOTIFICATIONS_FAILED);
			throw SmartLifeException.create(new IllegalStateException(TECHNICAL_MESSAGE),
					MessageCatalogProvider.getContent(USER_MESSAGE_CODE), TECHNICAL_MESSAGE);
		}
	}

}