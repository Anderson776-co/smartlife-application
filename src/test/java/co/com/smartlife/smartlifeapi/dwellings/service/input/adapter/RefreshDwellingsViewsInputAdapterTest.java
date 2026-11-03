package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper.RefreshDwellingsViewsMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.RefreshDwellingsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.RefreshDwellingsViewsUseCase;

class RefreshDwellingsViewsInputAdapterTest {

	@Test
	void shouldMapTheSourceNameToTheDomainAndRunTheUseCase() {
		final RefreshDwellingsViewsUseCase useCase = mock(RefreshDwellingsViewsUseCase.class);
		final RefreshDwellingsViewsInputAdapter adapter = new RefreshDwellingsViewsInputAdapter(useCase,
				Mappers.getMapper(RefreshDwellingsViewsMapperInput.class));

		adapter.execute(new RefreshDwellingsViewsDtoRequest("USERS"));

		final ArgumentCaptor<DwellingsViewRefreshDomain> captor = ArgumentCaptor.forClass(DwellingsViewRefreshDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(DwellingsViewRefreshDomain.DwellingsViewSource.USERS, captor.getValue().source());
	}

	@Test
	void anUnknownSourceShouldFailInsteadOfRefreshingNothingSilently() {
		final RefreshDwellingsViewsInputAdapter adapter = new RefreshDwellingsViewsInputAdapter(
				mock(RefreshDwellingsViewsUseCase.class), Mappers.getMapper(RefreshDwellingsViewsMapperInput.class));

		assertThrows(IllegalArgumentException.class, () -> adapter.execute(new RefreshDwellingsViewsDtoRequest("OTRO")));
	}
}
