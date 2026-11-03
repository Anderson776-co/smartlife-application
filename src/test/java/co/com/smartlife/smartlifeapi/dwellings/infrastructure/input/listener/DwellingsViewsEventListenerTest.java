package co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.listener.mapper.DwellingsEventMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.RefreshDwellingsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.RefreshDwellingsViewsInputPort;

class DwellingsViewsEventListenerTest {

	private RefreshDwellingsViewsInputPort port;
	private DwellingsViewsEventListener listener;

	@BeforeEach
	void setUp() {
		port = mock(RefreshDwellingsViewsInputPort.class);
		listener = new DwellingsViewsEventListener(port, Mappers.getMapper(DwellingsEventMapperInput.class));
	}

	private RefreshDwellingsViewsDtoRequest captured() {
		final ArgumentCaptor<RefreshDwellingsViewsDtoRequest> captor = ArgumentCaptor.forClass(RefreshDwellingsViewsDtoRequest.class);
		verify(port).execute(captor.capture());
		return captor.getValue();
	}

	@Test
	void theUserUpdatedEventShouldRefreshTheViewsOfUsers() {
		listener.on(new UserUpdatedEvent(UUID.randomUUID()));

		assertEquals("USERS", captured().source());
	}

	@Test
	void theUrbanizationUpdatedEventShouldRefreshTheViewsOfUrbanization() {
		listener.on(new UrbanizationUpdatedEvent(UUID.randomUUID()));

		assertEquals("URBANIZATION", captured().source());
	}
}
