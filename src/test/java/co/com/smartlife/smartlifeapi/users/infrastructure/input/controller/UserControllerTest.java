package co.com.smartlife.smartlifeapi.users.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.ChangeUserStatusDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.port.ChangeUserStatusInputPort;

class UserControllerTest {

	private static final UUID ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");

	@Test
	void shouldTakeTheIdFromThePathRunTheUpdateAndAnswer200() {
		final ChangeUserStatusInputPort port = mock(ChangeUserStatusInputPort.class);
		final UUID pathId = UUID.randomUUID();

		final ResponseEntity<Response<Void>> response = new UserController(port).changeStatus(pathId,
				new ChangeUserStatusDtoRequest(null, false));

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		final ArgumentCaptor<ChangeUserStatusDtoRequest> captor = ArgumentCaptor.forClass(ChangeUserStatusDtoRequest.class);
		verify(port).execute(captor.capture());
		assertEquals(pathId, captor.getValue().userId());
	}
}
