package co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener.mapper.ResidentsEventMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.RefreshResidentsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.input.port.RefreshResidentsViewsInputPort;

class ResidentsViewsEventListenerTest {

	private RefreshResidentsViewsInputPort port;
	private ResidentsViewsEventListener listener;

	@BeforeEach
	void setUp() {
		port = mock(RefreshResidentsViewsInputPort.class);
		listener = new ResidentsViewsEventListener(port, Mappers.getMapper(ResidentsEventMapperInput.class));
	}

	private RefreshResidentsViewsDtoRequest captured() {
		final ArgumentCaptor<RefreshResidentsViewsDtoRequest> captor = ArgumentCaptor.forClass(RefreshResidentsViewsDtoRequest.class);
		verify(port).execute(captor.capture());
		return captor.getValue();
	}

	@Test
	void theUserUpdatedEventShouldRefreshTheViewsOfUsers() {
		listener.on(new UserUpdatedEvent(UUID.randomUUID()));

		assertEquals("USERS", captured().source());
	}

	@Test
	void theDwellingUpdatedEventShouldRefreshTheViewsOfDwellings() {
		listener.on(new DwellingUpdatedEvent(UUID.randomUUID()));

		assertEquals("DWELLINGS", captured().source());
	}
}
