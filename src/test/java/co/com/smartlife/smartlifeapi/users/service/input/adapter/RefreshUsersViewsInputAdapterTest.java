package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.users.domain.UsersViewRefreshDomain;
import co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper.RefreshUsersViewsMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.RefreshUsersViewsDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.RefreshUsersViewsUseCase;

class RefreshUsersViewsInputAdapterTest {

	@Test
	void shouldMapTheSourceNameToTheDomainAndRunTheUseCase() {
		final RefreshUsersViewsUseCase useCase = mock(RefreshUsersViewsUseCase.class);
		final RefreshUsersViewsInputAdapter adapter = new RefreshUsersViewsInputAdapter(useCase,
				Mappers.getMapper(RefreshUsersViewsMapperInput.class));

		adapter.execute(new RefreshUsersViewsDtoRequest("URBANIZATION"));

		final ArgumentCaptor<UsersViewRefreshDomain> captor = ArgumentCaptor.forClass(UsersViewRefreshDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(UsersViewRefreshDomain.UsersViewSource.URBANIZATION, captor.getValue().source());
	}

	@Test
	void anUnknownSourceShouldFailInsteadOfRefreshingNothingSilently() {
		final RefreshUsersViewsInputAdapter adapter = new RefreshUsersViewsInputAdapter(
				mock(RefreshUsersViewsUseCase.class), Mappers.getMapper(RefreshUsersViewsMapperInput.class));

		assertThrows(IllegalArgumentException.class, () -> adapter.execute(new RefreshUsersViewsDtoRequest("OTRO")));
	}
}
