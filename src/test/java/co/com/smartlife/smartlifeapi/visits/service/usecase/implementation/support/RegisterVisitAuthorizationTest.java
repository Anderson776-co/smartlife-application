package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.TelemetryOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class RegisterVisitAuthorizationTest {

	private VisitorRepository visitorRepository;
	private VisitAuthorizationRepository authorizationRepository;
	private VisitAuthorizationNotifier notifier;
	private TelemetryOutputPort telemetry;
	private VisitAuthorizationEntity authorization;
	private VisitorEntity visitor;
	private RegisterVisitAuthorization register;

	@BeforeEach
	void setUp() {
		MessageCatalogProvider.init((code, locale) -> new MessageCatalogDTO(code, "", "texto:" + code, locale));
		visitorRepository = mock(VisitorRepository.class);
		authorizationRepository = mock(VisitAuthorizationRepository.class);
		notifier = mock(VisitAuthorizationNotifier.class);
		telemetry = mock(TelemetryOutputPort.class);
		authorization = mock(VisitAuthorizationEntity.class);
		visitor = mock(VisitorEntity.class);
		when(authorization.getVisitor()).thenReturn(visitor);
		register = new RegisterVisitAuthorization(visitorRepository, authorizationRepository, notifier, telemetry);
	}

	@Test
	void shouldCreateTheVisitorTheAuthorizationAndThenNotify() {
		when(notifier.notifyVisitor(authorization)).thenReturn(true);

		register.register(authorization, true);

		final InOrder order = inOrder(visitorRepository, authorizationRepository, notifier);
		order.verify(visitorRepository).create(visitor);
		order.verify(authorizationRepository).create(authorization);
		order.verify(notifier).notifyVisitor(authorization);
		verify(telemetry, never()).incrementCounter(RegisterVisitAuthorization.NOTIFICATIONS_FAILED);
	}

	@Test
	void shouldNotCreateTheVisitorAgainWhenItAlreadyExists() {
		when(notifier.notifyVisitor(authorization)).thenReturn(true);

		register.register(authorization, false);

		verify(visitorRepository, never()).create(visitor);
		verify(authorizationRepository).create(authorization);
	}

	@Test
	void shouldFailAndCountTheFailureWhenTheNotificationIsNotSent() {
		when(notifier.notifyVisitor(authorization)).thenReturn(false);

		assertThrows(SmartLifeException.class, () -> register.register(authorization, true));

		verify(telemetry).incrementCounter(RegisterVisitAuthorization.NOTIFICATIONS_FAILED);
	}
}