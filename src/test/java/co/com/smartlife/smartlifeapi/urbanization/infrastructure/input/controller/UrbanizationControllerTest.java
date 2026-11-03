package co.com.smartlife.smartlifeapi.urbanization.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request.RenameUrbanizationDtoRequest;
import co.com.smartlife.smartlifeapi.urbanization.service.input.port.RenameUrbanizationInputPort;

class UrbanizationControllerTest {

	private static final UUID ID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");

	@Test
	void shouldTakeTheIdFromThePathRunTheUpdateAndAnswer200() {
		final RenameUrbanizationInputPort port = mock(RenameUrbanizationInputPort.class);
		final UUID pathId = UUID.randomUUID();

		final ResponseEntity<Response<Void>> response = new UrbanizationController(port).rename(pathId,
				new RenameUrbanizationDtoRequest(null, "Conjunto nuevo"));

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		final ArgumentCaptor<RenameUrbanizationDtoRequest> captor = ArgumentCaptor.forClass(RenameUrbanizationDtoRequest.class);
		verify(port).execute(captor.capture());
		assertEquals(pathId, captor.getValue().urbanizationId());
	}
}
