package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.qr.ZxingQrGeneratorAdapter;

class ZxingQrGeneratorAdapterTest {

	private final ZxingQrGeneratorAdapter adapter = new ZxingQrGeneratorAdapter();

	@Test
	void generateBase64ShouldReturnNonEmptyText() {
		String result = adapter.generateBase64("AUTH-123");
		assertFalse(result.isBlank());
	}

	@Test
	void generateBase64ShouldReturnAPngImage() {
		byte[] image = Base64.getDecoder().decode(adapter.generateBase64("AUTH-123"));
		assertEquals((byte) 0x89, image[0]);
		assertEquals((byte) 'P', image[1]);
		assertEquals((byte) 'N', image[2]);
		assertEquals((byte) 'G', image[3]);
	}
}