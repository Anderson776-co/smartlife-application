package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.visits.domain.VisitsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.RefreshVisitsViewsMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.RefreshVisitsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.RefreshVisitsViewsUseCase;

class RefreshVisitsViewsInputAdapterTest {

	@Test
	void shouldMapTheSourceNameToTheDomainAndRunTheUseCase() {
		final RefreshVisitsViewsUseCase useCase = mock(RefreshVisitsViewsUseCase.class);
		final RefreshVisitsViewsInputAdapter adapter = new RefreshVisitsViewsInputAdapter(useCase,
				Mappers.getMapper(RefreshVisitsViewsMapperInput.class));

		adapter.execute(new RefreshVisitsViewsDtoRequest("USERS"));

		final ArgumentCaptor<VisitsViewRefreshDomain> captor = ArgumentCaptor.forClass(VisitsViewRefreshDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(VisitsViewRefreshDomain.VisitsViewSource.USERS, captor.getValue().source());
	}

	@Test
	void anUnknownSourceShouldFailInsteadOfRefreshingNothingSilently() {
		final RefreshVisitsViewsInputAdapter adapter = new RefreshVisitsViewsInputAdapter(
				mock(RefreshVisitsViewsUseCase.class), Mappers.getMapper(RefreshVisitsViewsMapperInput.class));

		assertThrows(IllegalArgumentException.class, () -> adapter.execute(new RefreshVisitsViewsDtoRequest("OTRO")));
	}
}
