package co.com.smartlife.smartlifeapi.users.infrastructure.input.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.users.infrastructure.input.listener.mapper.UsersEventMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.RefreshUsersViewsDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.port.RefreshUsersViewsInputPort;

class UsersViewsEventListenerTest {

	private RefreshUsersViewsInputPort port;
	private UsersViewsEventListener listener;

	@BeforeEach
	void setUp() {
		port = mock(RefreshUsersViewsInputPort.class);
		listener = new UsersViewsEventListener(port, Mappers.getMapper(UsersEventMapperInput.class));
	}

	private RefreshUsersViewsDtoRequest captured() {
		final ArgumentCaptor<RefreshUsersViewsDtoRequest> captor = ArgumentCaptor.forClass(RefreshUsersViewsDtoRequest.class);
		verify(port).execute(captor.capture());
		return captor.getValue();
	}

	@Test
	void theUrbanizationUpdatedEventShouldRefreshTheViewsOfUrbanization() {
		listener.on(new UrbanizationUpdatedEvent(UUID.randomUUID()));

		assertEquals("URBANIZATION", captured().source());
	}
}
