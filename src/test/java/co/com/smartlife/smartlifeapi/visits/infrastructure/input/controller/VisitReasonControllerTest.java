package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultVisitReasonsInputPort;

class VisitReasonControllerTest {

	private ConsultVisitReasonsInputPort port;
	private VisitReasonController controller;

	@BeforeEach
	void setUp() {
		port = mock(ConsultVisitReasonsInputPort.class);
		controller = new VisitReasonController(port);
	}

	@Test
	void shouldRespondWithEveryVisitReason() {
		final VisitReasonDtoResponse family = new VisitReasonDtoResponse(UUID.randomUUID(), "Visita familiar", false);
		when(port.execute()).thenReturn(List.of(family));

		final ResponseEntity<Response<VisitReasonDtoResponse>> response = controller.list();

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertEquals(List.of(family), response.getBody().getData());
	}

	@Test
	void shouldRespondSuccessfullyWithoutDataWhenThereAreNoReasons() {
		when(port.execute()).thenReturn(List.of());

		final ResponseEntity<Response<VisitReasonDtoResponse>> response = controller.list();

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertTrue(response.getBody().getData().isEmpty());
	}

}
