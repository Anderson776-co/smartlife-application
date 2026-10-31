package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.FileStorageOutputPort;

class VisitDocumentStorageTest {

	private FileStorageOutputPort fileStorage;
	private VisitDocumentStorage storage;

	@BeforeEach
	void setUp() {
		fileStorage = mock(FileStorageOutputPort.class);
		storage = new VisitDocumentStorage(fileStorage);
	}

	@Test
	void shouldBuildTheKeyFromTheAuthorizationIdAndTheValidatedType() {
		final UUID id = UUID.randomUUID();
		final byte[] content = { 1, 2, 3 };
		final String key = "visits/authorizations/" + id + "/document.pdf";
		when(fileStorage.upload(key, content, "application/pdf")).thenReturn(key);

		final String reference = storage.store(id, new AttachedDocumentDomain("../../etc/passwd", "application/pdf", content));

		assertEquals(key, reference);
	}

	@Test
	void shouldWrapStorageFailuresInATechnicalException() {
		final RuntimeException cause = new IllegalStateException("r2 down");
		when(fileStorage.upload(anyString(), any(), anyString())).thenThrow(cause);

		final SmartLifeException exception = assertThrows(SmartLifeException.class,
				() -> storage.store(UUID.randomUUID(), new AttachedDocumentDomain("a", "image/png", new byte[] { 1 })));

		assertEquals(SmartLifeException.Type.TECHNICAL, exception.getType());
		assertSame(cause, exception.getRootException());
	}

	@Test
	void shouldDeleteTheDocumentOnDiscard() {
		storage.discard("some/key");

		verify(fileStorage).delete("some/key");
	}

	@Test
	void shouldNotPropagateFailuresWhenDiscarding() {
		doThrow(new IllegalStateException("r2 down")).when(fileStorage).delete("some/key");

		assertDoesNotThrow(() -> storage.discard("some/key"));
	}

}
