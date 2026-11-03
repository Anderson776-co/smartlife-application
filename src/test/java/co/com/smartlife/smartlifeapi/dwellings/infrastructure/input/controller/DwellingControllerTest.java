package co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.ChangeDwellingOwnerInputPort;

class DwellingControllerTest {

	private static final UUID ID = UUID.fromString("cccccccc-0000-0000-0000-000000000001");
	private static final UUID OWNER = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");

	@Test
	void shouldTakeTheIdFromThePathRunTheUpdateAndAnswer200() {
		final ChangeDwellingOwnerInputPort port = mock(ChangeDwellingOwnerInputPort.class);
		final UUID pathId = UUID.randomUUID();

		final ResponseEntity<Response<Void>> response = new DwellingController(port).changeOwner(pathId,
				new ChangeDwellingOwnerDtoRequest(null, OWNER));

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		final ArgumentCaptor<ChangeDwellingOwnerDtoRequest> captor = ArgumentCaptor.forClass(ChangeDwellingOwnerDtoRequest.class);
		verify(port).execute(captor.capture());
		assertEquals(pathId, captor.getValue().dwellingId());
	}
}
