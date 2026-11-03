package co.com.smartlife.smartlifeapi.residents.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.ChangeResidentResidenceStatusDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.input.port.ChangeResidentResidenceStatusInputPort;

class ResidentResidenceControllerTest {

	private static final UUID ID = UUID.fromString("99999999-0000-0000-0000-000000000001");

	@Test
	void shouldTakeTheIdFromThePathRunTheUpdateAndAnswer200() {
		final ChangeResidentResidenceStatusInputPort port = mock(ChangeResidentResidenceStatusInputPort.class);
		final UUID pathId = UUID.randomUUID();

		final ResponseEntity<Response<Void>> response = new ResidentResidenceController(port).changeStatus(pathId,
				new ChangeResidentResidenceStatusDtoRequest(null, true));

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		final ArgumentCaptor<ChangeResidentResidenceStatusDtoRequest> captor = ArgumentCaptor.forClass(ChangeResidentResidenceStatusDtoRequest.class);
		verify(port).execute(captor.capture());
		assertEquals(pathId, captor.getValue().residentResidenceId());
	}
}
