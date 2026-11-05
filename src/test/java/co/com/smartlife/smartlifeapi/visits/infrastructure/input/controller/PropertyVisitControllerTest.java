package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultAuthorizatorPropertiesInputPort;

class PropertyVisitControllerTest {

	private static final UUID USER_ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
	private static final UUID PROPERTY_ID = UUID.fromString("cccccccc-0000-0000-0000-000000000002");
	private static final CurrentUser USER = new CurrentUser(USER_ID, "maria@example.com", Set.of("RESIDENT"),
			UUID.randomUUID());

	private ConsultAuthorizatorPropertiesInputPort port;
	private PropertyVisitController controller;

	@BeforeEach
	void setUp() {
		port = mock(ConsultAuthorizatorPropertiesInputPort.class);
		controller = new PropertyVisitController(port);
	}

	@Test
	void shouldReturnOnlyThePropertiesTheSessionUserCanAuthorizeVisitsOn() {
		final PropertyVisitDtoResponse apartment202 = new PropertyVisitDtoResponse(PROPERTY_ID,
				"Bloque B, Vivienda 202, Torre 1 piso 2");
		when(port.execute(new ListAuthorizatorPropertiesDtoRequest(USER))).thenReturn(List.of(apartment202));

		final ResponseEntity<Response<PropertyVisitDtoResponse>> response = controller.list(USER);

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertEquals(List.of(apartment202), response.getBody().getData());
	}

	@Test
	void shouldAskForThePropertiesOfTheUserInSessionAndNotOfAnyoneElse() {
		when(port.execute(new ListAuthorizatorPropertiesDtoRequest(USER))).thenReturn(List.of());

		controller.list(USER);

		final ArgumentCaptor<ListAuthorizatorPropertiesDtoRequest> captor = ArgumentCaptor
				.forClass(ListAuthorizatorPropertiesDtoRequest.class);
		verify(port).execute(captor.capture());
		assertEquals(USER, captor.getValue().currentUser());
	}

	@Test
	void shouldRespondSuccessfullyWithoutDataWhenTheUserHasNoProperties() {
		when(port.execute(new ListAuthorizatorPropertiesDtoRequest(USER))).thenReturn(List.of());

		final ResponseEntity<Response<PropertyVisitDtoResponse>> response = controller.list(USER);

		assertEquals(200, response.getStatusCode().value());
		assertTrue(response.getBody().isResponseSucceded());
		assertTrue(response.getBody().getData().isEmpty());
	}

}
