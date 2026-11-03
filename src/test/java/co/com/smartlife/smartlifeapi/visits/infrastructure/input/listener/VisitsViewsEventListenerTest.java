package co.com.smartlife.smartlifeapi.visits.infrastructure.input.listener;

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
import co.com.smartlife.smartlifeapi.residents.event.ResidentResidenceUpdatedEvent;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.listener.mapper.VisitsEventMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.RefreshVisitsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.RefreshVisitsViewsInputPort;

class VisitsViewsEventListenerTest {

	private RefreshVisitsViewsInputPort port;
	private VisitsViewsEventListener listener;

	@BeforeEach
	void setUp() {
		port = mock(RefreshVisitsViewsInputPort.class);
		listener = new VisitsViewsEventListener(port, Mappers.getMapper(VisitsEventMapperInput.class));
	}

	private RefreshVisitsViewsDtoRequest captured() {
		final ArgumentCaptor<RefreshVisitsViewsDtoRequest> captor = ArgumentCaptor.forClass(RefreshVisitsViewsDtoRequest.class);
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

	@Test
	void theResidentResidenceUpdatedEventShouldRefreshTheViewsOfResidents() {
		listener.on(new ResidentResidenceUpdatedEvent(UUID.randomUUID(), UUID.randomUUID()));

		assertEquals("RESIDENTS", captured().source());
	}
}
