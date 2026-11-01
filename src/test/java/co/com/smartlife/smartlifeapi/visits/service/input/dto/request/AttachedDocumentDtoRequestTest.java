package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AttachedDocumentDtoRequestTest {

	private static AttachedDocumentDtoRequest pdf(final byte... content) {
		return new AttachedDocumentDtoRequest("orden.pdf", "application/pdf", content);
	}

	@Test
	void shouldBeEqualWhenTheContentIsTheSameEvenIfTheArraysAreDifferentObjects() {
		final AttachedDocumentDtoRequest first = pdf((byte) 1, (byte) 2, (byte) 3);
		final AttachedDocumentDtoRequest second = pdf((byte) 1, (byte) 2, (byte) 3);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldNotBeEqualWhenTheContentOrTheNameDiffers() {
		final AttachedDocumentDtoRequest base = pdf((byte) 1, (byte) 2);

		assertNotEquals(base, pdf((byte) 1, (byte) 3));
		assertNotEquals(base, new AttachedDocumentDtoRequest("otra.pdf", "application/pdf", new byte[] { 1, 2 }));
		assertNotEquals(base, null);
		assertNotEquals(base, "no es un documento");
		assertEquals(base, base);
	}

	@Test
	void shouldNormalizeTheFieldsAndTheEmptyContent() {
		final AttachedDocumentDtoRequest document = new AttachedDocumentDtoRequest("  a.png ", " IMAGE/PNG ", null);

		assertEquals("a.png", document.fileName());
		assertEquals("image/png", document.contentType());
		assertFalse(document.hasContent());
		assertFalse(AttachedDocumentDtoRequest.empty().hasContent());
		assertTrue(pdf((byte) 9).hasContent());
	}

	@Test
	void toStringShouldShowTheSizeAndNotTheContent() {
		final String text = pdf((byte) 1, (byte) 2, (byte) 3).toString();

		assertTrue(text.contains("contentLength=3"));
		assertTrue(text.contains("orden.pdf"));
		assertFalse(text.contains("[1, 2, 3]"));
	}
}