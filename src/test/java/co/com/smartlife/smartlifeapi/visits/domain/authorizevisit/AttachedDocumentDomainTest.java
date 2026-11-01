package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AttachedDocumentDomainTest {

	private static AttachedDocumentDomain pdf(final byte... content) {
		return new AttachedDocumentDomain("orden.pdf", "application/pdf", content);
	}

	@Test
	void shouldBeEqualWhenTheContentIsTheSameEvenIfTheArraysAreDifferentObjects() {
		final AttachedDocumentDomain first = pdf((byte) 1, (byte) 2, (byte) 3);
		final AttachedDocumentDomain second = pdf((byte) 1, (byte) 2, (byte) 3);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldNotBeEqualWhenTheContentOrTheNameDiffers() {
		final AttachedDocumentDomain base = pdf((byte) 1, (byte) 2);

		assertNotEquals(base, pdf((byte) 1, (byte) 3));
		assertNotEquals(base, new AttachedDocumentDomain("otra.pdf", "application/pdf", new byte[] { 1, 2 }));
		assertNotEquals(base, null);
		assertNotEquals(base, "no es un documento");
		assertEquals(base, base);
	}

	@Test
	void hasContentShouldDependOnTheSizeOfTheContent() {
		assertTrue(pdf((byte) 9).hasContent());
		assertFalse(pdf().hasContent());
	}

	@Test
	void toStringShouldShowTheSizeAndNotTheContent() {
		final String text = pdf((byte) 1, (byte) 2, (byte) 3).toString();

		assertTrue(text.contains("contentLength=3"));
		assertTrue(text.contains("orden.pdf"));
		assertFalse(text.contains("[1, 2, 3]"));
	}
}