package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class VisitAuthorizationDomainTest {

	private static final Instant START = Instant.parse("2026-11-03T10:00:00Z");

	private static VisitAuthorizationDomain withoutDisplayData() {
		return new VisitAuthorizationDomain(UUID.randomUUID(), START, START.plusSeconds(3600), START, "", "qr", "",
				AttachedDocumentDomain.empty(), UUID.randomUUID(), UUID.randomUUID(),
				new AuthorizatorDomain(UUID.randomUUID(), UUID.randomUUID(), Set.of()),
				new VisitorDomain(UUID.randomUUID(), "2001", "Luis", "", "Torres", "", "luis@example.com", ""), false);
	}

	@Test
	void theConstructorUsedToAuthorizeLeavesTheDisplayDataWithItsDefaults() {
		final VisitAuthorizationDomain domain = withoutDisplayData();

		assertEquals("", domain.propertyLocation());
		assertEquals("", domain.visitReasonName());
		assertEquals(VisitAuthorizationStatus.UNSPECIFIED, domain.status());
	}

	@Test
	void theDisplayDataThatIsInformedIsKept() {
		final VisitAuthorizationDomain base = withoutDisplayData();

		final VisitAuthorizationDomain domain = new VisitAuthorizationDomain(base.id(), base.authorizationStartDate(),
				base.authorizationEndDate(), base.registerDate(), base.observation(), base.qrCode(),
				base.fileReference(), base.attachedDocument(), base.propertyId(), base.visitReasonId(),
				base.authorizator(), base.visitor(), base.notificationSent(), "Apartamento 202", "Servicio tecnico",
				VisitAuthorizationStatus.CURRENT);

		assertEquals("Apartamento 202", domain.propertyLocation());
		assertEquals("Servicio tecnico", domain.visitReasonName());
		assertEquals(VisitAuthorizationStatus.CURRENT, domain.status());
	}

	@Test
	void onlyTheUnspecifiedStatusIsNotARealStatus() {
		assertFalse(VisitAuthorizationStatus.UNSPECIFIED.isSpecified());
		assertTrue(VisitAuthorizationStatus.UPCOMING.isSpecified());
		assertTrue(VisitAuthorizationStatus.CURRENT.isSpecified());
		assertTrue(VisitAuthorizationStatus.EXPIRED.isSpecified());
		assertTrue(VisitAuthorizationStatus.CANCELLED.isSpecified());
	}
}
