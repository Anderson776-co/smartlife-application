package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.users.domain.UserStatusChangeDomain;
import co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper.UserStatusMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.ChangeUserStatusDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.ChangeUserStatusUseCase;

class ChangeUserStatusInputAdapterTest {

	private static final UUID ID = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");

	@Test
	void shouldMapTheRequestToTheDomainAndRunTheUseCase() {
		final ChangeUserStatusUseCase useCase = mock(ChangeUserStatusUseCase.class);
		final ChangeUserStatusInputAdapter adapter = new ChangeUserStatusInputAdapter(useCase, Mappers.getMapper(UserStatusMapperInput.class));

		adapter.execute(new ChangeUserStatusDtoRequest(ID, false));

		final ArgumentCaptor<UserStatusChangeDomain> captor = ArgumentCaptor.forClass(UserStatusChangeDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(ID, captor.getValue().userId());
		assertEquals(Boolean.FALSE, captor.getValue().active());
	}
}
