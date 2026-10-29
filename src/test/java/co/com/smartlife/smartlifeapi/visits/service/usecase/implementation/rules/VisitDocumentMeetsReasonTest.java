package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

class VisitDocumentMeetsReasonTest {

	private static final VisitParametersDTO PARAMETERS = new VisitParametersDTO(Duration.ofHours(24),
			Duration.ofDays(30), Duration.ofMinutes(10), 32, Set.of("application/pdf", "image/png"), "http://qr/",
			ZoneOffset.UTC);
	private static final VisitReasonEntity REQUIRES = new VisitReasonEntity(UUID.randomUUID(), "Servicio tecnico", true);
	private static final VisitReasonEntity DOES_NOT_REQUIRE = new VisitReasonEntity(UUID.randomUUID(), "Visita", false);

	@Test
	void shouldNotDemandADocumentWhenTheReasonDoesNotRequireIt() {
		assertDoesNotThrow(() -> run(DOES_NOT_REQUIRE, AttachedDocumentDomain.empty()));
	}

	@Test
	void shouldAcceptAValidPdf() {
		assertDoesNotThrow(() -> run(REQUIRES, document("application/pdf", "%PDF-1.7")));
	}

	@Test
	void shouldAcceptAValidPng() {
		final byte[] png = { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A };
		assertDoesNotThrow(() -> run(REQUIRES, new AttachedDocumentDomain("a.png", "image/png", png)));
	}

	@Test
	void shouldRejectAMissingDocument() {
		assertThrows(SmartLifeException.class, () -> run(REQUIRES, AttachedDocumentDomain.empty()));
		assertThrows(SmartLifeException.class, () -> run(REQUIRES, null));
	}

	@Test
	void shouldRejectATypeThatIsNotAllowed() {
		assertThrows(SmartLifeException.class, () -> run(REQUIRES, document("image/gif", "GIF89a")));
	}

	@Test
	void shouldRejectContentThatDoesNotMatchTheDeclaredType() {
		assertThrows(SmartLifeException.class, () -> run(REQUIRES, document("application/pdf", "no es un pdf")));
	}

	@Test
	void shouldRejectADocumentOverTheMaximumSize() {
		assertThrows(SmartLifeException.class,
				() -> run(REQUIRES, document("application/pdf", "%PDF-" + "x".repeat(40))));
	}

	@Test
	void shouldRejectIncompleteData() {
		assertThrows(SmartLifeException.class, () -> VisitDocumentMeetsReason.executeRule(REQUIRES));
	}

	private static void run(final VisitReasonEntity reason, final AttachedDocumentDomain document) {
		VisitDocumentMeetsReason.executeRule(reason, document, PARAMETERS);
	}

	private static AttachedDocumentDomain document(final String contentType, final String content) {
		return new AttachedDocumentDomain("file", contentType, content.getBytes(StandardCharsets.ISO_8859_1));
	}

}
