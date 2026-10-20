package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.filestorage.r2.adapter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class R2FileStorageAdapterTest {

	private static final class FakeS3Client implements S3Client {

		private final byte[] storedContent;
		private PutObjectRequest lastPutRequest;
		private byte[] lastPutBody;
		private GetObjectRequest lastGetRequest;

		FakeS3Client(final byte[] storedContent) {
			this.storedContent = storedContent;
		}

		@Override
		public String serviceName() {
			return "s3";
		}

		@Override
		public void close() {
			// No se requiere liberar recursos porque este cliente simulado no mantiene conexiones.
		}

		@Override
		public PutObjectResponse putObject(final PutObjectRequest request, final RequestBody body) {
			lastPutRequest = request;
			try {
				lastPutBody = body.contentStreamProvider().newStream().readAllBytes();
			} catch (IOException exception) {
				throw new IllegalStateException(exception);
			}
			return PutObjectResponse.builder().build();
		}

		@Override
		public ResponseBytes<GetObjectResponse> getObjectAsBytes(final GetObjectRequest request) {
			lastGetRequest = request;
			return ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), storedContent);
		}
	}

	@Test
	void uploadShouldSendTheFileWithItsKeyAndReturnTheKey() {
		FakeS3Client fake = new FakeS3Client(new byte[0]);
		R2FileStorageAdapter adapter = new R2FileStorageAdapter(fake, "permits-bucket");
		byte[] content = "contenido del permiso".getBytes(StandardCharsets.UTF_8);

		String key = adapter.upload("permits/permiso-123.pdf", content, "application/pdf");

		assertEquals("permits/permiso-123.pdf", key);
		assertEquals("permits-bucket", fake.lastPutRequest.bucket());
		assertEquals("permits/permiso-123.pdf", fake.lastPutRequest.key());
		assertEquals("application/pdf", fake.lastPutRequest.contentType());
		assertArrayEquals(content, fake.lastPutBody);
	}

	@Test
	void downloadShouldReturnTheStoredContent() {
		byte[] stored = "archivo guardado".getBytes(StandardCharsets.UTF_8);
		FakeS3Client fake = new FakeS3Client(stored);
		R2FileStorageAdapter adapter = new R2FileStorageAdapter(fake, "permits-bucket");

		byte[] result = adapter.download("permits/permiso-123.pdf");

		assertArrayEquals(stored, result);
		assertEquals("permits-bucket", fake.lastGetRequest.bucket());
		assertEquals("permits/permiso-123.pdf", fake.lastGetRequest.key());
	}

	@Test
	void uploadShouldFailWhenTheBucketIsNotConfigured() {
		R2FileStorageAdapter adapter = new R2FileStorageAdapter(new FakeS3Client(new byte[0]), "");

		assertThrows(IllegalStateException.class, () -> adapter.upload("llave", new byte[] { 1 }, "text/plain"));
	}

	@Test
	void uploadShouldFailWhenR2IsNotConfigured() {
		R2FileStorageAdapter adapter = new R2FileStorageAdapter("", "", "", "permits-bucket");

		assertThrows(IllegalStateException.class, () -> adapter.upload("llave", new byte[] { 1 }, "text/plain"));
	}
}