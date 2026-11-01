package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VisitorDtoRequestTest {

	@Test
	void shouldTrimTheFieldsAndNormalizeTheEmailAndThePlate() {
		final VisitorDtoRequest visitor = new VisitorDtoRequest("  2001 ", " Luis ", " Andres ", " Torres ", " Gomez ",
				"  LUIS@Example.COM ", " abc-123 ");

		assertEquals("2001", visitor.documentNumber());
		assertEquals("Luis", visitor.firstName());
		assertEquals("Andres", visitor.middleName());
		assertEquals("Torres", visitor.lastName());
		assertEquals("Gomez", visitor.secondLastName());
		assertEquals("luis@example.com", visitor.email());
		assertEquals("ABC-123", visitor.vehiclePlate());
	}

	@Test
	void shouldReplaceNullsWithEmptyText() {
		final VisitorDtoRequest visitor = new VisitorDtoRequest(null, null, null, null, null, null, null);

		assertEquals("", visitor.documentNumber());
		assertEquals("", visitor.firstName());
		assertEquals("", visitor.middleName());
		assertEquals("", visitor.lastName());
		assertEquals("", visitor.secondLastName());
		assertEquals("", visitor.email());
		assertEquals("", visitor.vehiclePlate());
	}

	@Test
	void emptyShouldBeTheSameAsAVisitorWithoutData() {
		assertEquals(new VisitorDtoRequest(null, null, null, null, null, null, null), VisitorDtoRequest.empty());
	}
}