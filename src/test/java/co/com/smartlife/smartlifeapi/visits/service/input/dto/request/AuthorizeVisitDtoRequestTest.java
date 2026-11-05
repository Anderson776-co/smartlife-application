package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;

class AuthorizeVisitDtoRequestTest {

	private static final UUID DEFAULT_ID = UUIDHelper.getUUIDHelper().getDefault();

	@Test
	void shouldApplyTheDefaultsWhenEverythingIsMissing() {
		final AuthorizeVisitDtoRequest request = new AuthorizeVisitDtoRequest(null, null, null, null, null, null, null,
				null);

		assertEquals(DEFAULT_ID, request.propertyId());
		assertEquals(DEFAULT_ID, request.visitReasonId());
		assertEquals(Instant.EPOCH, request.authorizationStartDate());
		assertEquals(Instant.EPOCH, request.authorizationEndDate());
		assertEquals("", request.observation());
		assertEquals(VisitorDtoRequest.empty(), request.visitor());
		assertEquals(AttachedDocumentDtoRequest.empty(), request.attachedDocument());
		assertEquals(new CurrentUser(DEFAULT_ID, "", Set.of(), DEFAULT_ID), request.authorizator());
	}

	@Test
	void shouldKeepTheGivenValues() {
		final UUID propertyId = UUID.randomUUID();
		final UUID reasonId = UUID.randomUUID();
		final Instant start = Instant.parse("2026-10-29T15:00:00Z");
		final Instant end = Instant.parse("2026-10-29T18:00:00Z");
		final VisitorDtoRequest visitor = new VisitorDtoRequest("2001", "Luis", "", "Torres", "", "luis@example.com", "");
		final AttachedDocumentDtoRequest document = new AttachedDocumentDtoRequest("a.pdf", "application/pdf",
				new byte[] { 1 });
		final CurrentUser user = new CurrentUser(UUID.randomUUID(), "ana@example.com", Set.of("RESIDENT"), UUID.randomUUID());

		final AuthorizeVisitDtoRequest request = new AuthorizeVisitDtoRequest(propertyId, reasonId, start, end,
				"Entrega", visitor, document, user);

		assertEquals(propertyId, request.propertyId());
		assertEquals(reasonId, request.visitReasonId());
		assertEquals(start, request.authorizationStartDate());
		assertEquals(end, request.authorizationEndDate());
		assertEquals("Entrega", request.observation());
		assertSame(visitor, request.visitor());
		assertSame(document, request.attachedDocument());
		assertSame(user, request.authorizator());
	}

	@Test
	void withContextShouldReplaceOnlyTheDocumentAndTheCurrentUser() {
		final UUID propertyId = UUID.randomUUID();
		final VisitorDtoRequest visitor = new VisitorDtoRequest("2001", "Luis", "", "Torres", "", "luis@example.com", "");
		final AuthorizeVisitDtoRequest original = new AuthorizeVisitDtoRequest(propertyId, UUID.randomUUID(),
				Instant.EPOCH, Instant.EPOCH, "Entrega", visitor, null, null);
		final AttachedDocumentDtoRequest document = new AttachedDocumentDtoRequest("a.pdf", "application/pdf",
				new byte[] { 1 });
		final CurrentUser user = new CurrentUser(UUID.randomUUID(), "ana@example.com", Set.of("RESIDENT"), UUID.randomUUID());

		final AuthorizeVisitDtoRequest withContext = original.withContext(document, user);

		assertSame(document, withContext.attachedDocument());
		assertSame(user, withContext.authorizator());
		assertEquals(propertyId, withContext.propertyId());
		assertEquals("Entrega", withContext.observation());
		assertSame(visitor, withContext.visitor());
	}
}