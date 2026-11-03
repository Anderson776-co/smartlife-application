package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalogTestSupport;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationCatalogOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationGatewayOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.QrGeneratorOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailAttachmentDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class VisitAuthorizationNotifierTest {

	private static final String CODE = "VISIT_AUTHORIZATION_CREATED";
	private static final String QR_URL = "https://smartlife.sarl/visitas/autorizaciones/abc";
	private static final Instant START = Instant.parse("2026-10-29T15:00:00Z");
	private static final Instant END = Instant.parse("2026-10-29T18:00:00Z");

	private NotificationCatalogOutputPort catalog;
	private NotificationGatewayOutputPort gateway;
	private QrGeneratorOutputPort qrGenerator;
	private VisitAuthorizationNotifier notifier;

	@BeforeEach
	void setUp() {
		catalog = mock(NotificationCatalogOutputPort.class);
		gateway = mock(NotificationGatewayOutputPort.class);
		qrGenerator = mock(QrGeneratorOutputPort.class);
		ParameterCatalogTestSupport.load();
		notifier = new VisitAuthorizationNotifier(catalog, gateway, qrGenerator);

		when(catalog.findByCodeAndLocale(eq(CODE), anyString())).thenReturn(new NotificationCatalogDTO(CODE,
				"Visita para {{visitorName}}",
				"<p>{{visitorName}} fue autorizado por {{authorizatorName}} en {{location}} por {{reason}}, "
						+ "desde {{startDate}} hasta {{endDate}}, placa {{vehiclePlate}}</p>"
						+ "<img src=\"cid:visit-qr\"><a href=\"{{qrUrl}}\">QR</a>",
				"es"));
		when(qrGenerator.generateBase64(QR_URL)).thenReturn("QkFTRTY0");
		when(gateway.sendEmail(any())).thenReturn("email-1");
	}

	private static VisitorEntity visitor(final String first, final String middle, final String last,
			final String secondLast, final String plate) {
		return new VisitorEntity(UUID.randomUUID(), first, middle, last, secondLast, "2001", "luis@example.com", plate);
	}

	private static VisitAuthorizationEntity authorization(final VisitorEntity visitor) {
		final UUID authorizatorId = UUID.randomUUID();
		return new VisitAuthorizationEntity(UUID.randomUUID(), START, END, START, "Entrega", QR_URL, null,
				Boolean.TRUE, new PropertyVisitEntity(UUID.randomUUID(), "Torre 1 - 101", authorizatorId),
				new AuthorizatorEntity(authorizatorId, "Ana Ruiz", true, UUID.randomUUID()), visitor,
				new VisitReasonEntity(UUID.randomUUID(), "Visita familiar", false));
	}

	private EmailNotificationDTO sentEmail() {
		final ArgumentCaptor<EmailNotificationDTO> captor = ArgumentCaptor.forClass(EmailNotificationDTO.class);
		verify(gateway).sendEmail(captor.capture());
		return captor.getValue();
	}

	@Test
	void shouldSendTheEmailWithTheRenderedTemplateAndTheQrAttachment() {
		final boolean sent = notifier
				.notifyVisitor(authorization(visitor("Luis", "Andres", "Torres", "Gomez", "ABC123")));

		assertTrue(sent);
		final EmailNotificationDTO email = sentEmail();
		assertEquals("luis@example.com", email.to());
		assertEquals("Visita para Luis Andres Torres Gomez", email.subject());
		assertTrue(email.content().contains("Ana Ruiz"));
		assertTrue(email.content().contains("Torre 1 - 101"));
		assertTrue(email.content().contains("Visita familiar"));
		assertTrue(email.content().contains("29/10/2026 03:00"));
		assertTrue(email.content().contains("29/10/2026 06:00"));
		assertTrue(email.content().contains("ABC123"));
		assertTrue(email.content().contains(QR_URL));
		assertTrue(email.content().contains("cid:visit-qr"));
		assertFalse(email.content().contains("{{"));

		assertEquals(1, email.attachments().size());
		final EmailAttachmentDTO attachment = email.attachments().get(0);
		assertEquals("visit-qr", attachment.contentId());
		assertEquals("QkFTRTY0", attachment.base64Content());
		assertEquals("image/png", attachment.contentType());
		assertEquals("autorizacion-visita.png", attachment.fileName());
	}

	@Test
	void shouldSkipTheEmptyNamePartsAndTheMissingPlate() {
		final boolean sent = notifier.notifyVisitor(authorization(visitor("Luis", "", "Torres", null, null)));

		assertTrue(sent);
		final EmailNotificationDTO email = sentEmail();
		assertEquals("Visita para Luis Torres", email.subject());
		assertFalse(email.content().contains("{{"));
		assertFalse(email.content().contains("null"));
	}

	@Test
	void shouldEscapeTheHtmlOfTheDataInTheBodyButNotInTheSubject() {
		final boolean sent = notifier.notifyVisitor(authorization(visitor("<b>Luis</b>", "", "Torres", "", "ABC123")));

		assertTrue(sent);
		final EmailNotificationDTO email = sentEmail();
		assertTrue(email.content().contains("&lt;b&gt;Luis&lt;/b&gt;"));
		assertFalse(email.content().contains("<b>Luis</b>"));
		assertTrue(email.subject().contains("<b>Luis</b>"));
	}

	@Test
	void shouldReturnFalseWhenTheEmailCannotBeSent() {
		when(gateway.sendEmail(any())).thenThrow(new IllegalStateException("resend no responde"));

		assertFalse(notifier.notifyVisitor(authorization(visitor("Luis", "", "Torres", "", ""))));
	}

	@Test
	void shouldReturnFalseWhenTheTemplateIsNotAvailable() {
		when(catalog.findByCodeAndLocale(anyString(), anyString())).thenThrow(new IllegalStateException("strapi caido"));

		assertFalse(notifier.notifyVisitor(authorization(visitor("Luis", "", "Torres", "", ""))));
		verify(gateway, never()).sendEmail(any());
	}

	@Test
	void shouldReturnFalseWhenTheQrCannotBeGenerated() {
		when(qrGenerator.generateBase64(anyString())).thenThrow(new IllegalArgumentException("sin qr"));

		assertFalse(notifier.notifyVisitor(authorization(visitor("Luis", "", "Torres", "", ""))));
		verify(gateway, never()).sendEmail(any());
	}
}