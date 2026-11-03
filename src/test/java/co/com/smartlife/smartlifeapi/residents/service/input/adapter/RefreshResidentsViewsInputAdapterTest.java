package co.com.smartlife.smartlifeapi.residents.service.input.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import co.com.smartlife.smartlifeapi.residents.domain.ResidentsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper.RefreshResidentsViewsMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.RefreshResidentsViewsDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.RefreshResidentsViewsUseCase;

class RefreshResidentsViewsInputAdapterTest {

	@Test
	void shouldMapTheSourceNameToTheDomainAndRunTheUseCase() {
		final RefreshResidentsViewsUseCase useCase = mock(RefreshResidentsViewsUseCase.class);
		final RefreshResidentsViewsInputAdapter adapter = new RefreshResidentsViewsInputAdapter(useCase,
				Mappers.getMapper(RefreshResidentsViewsMapperInput.class));

		adapter.execute(new RefreshResidentsViewsDtoRequest("USERS"));

		final ArgumentCaptor<ResidentsViewRefreshDomain> captor = ArgumentCaptor.forClass(ResidentsViewRefreshDomain.class);
		verify(useCase).execute(captor.capture());
		assertEquals(ResidentsViewRefreshDomain.ResidentsViewSource.USERS, captor.getValue().source());
	}

	@Test
	void anUnknownSourceShouldFailInsteadOfRefreshingNothingSilently() {
		final RefreshResidentsViewsInputAdapter adapter = new RefreshResidentsViewsInputAdapter(
				mock(RefreshResidentsViewsUseCase.class), Mappers.getMapper(RefreshResidentsViewsMapperInput.class));

		assertThrows(IllegalArgumentException.class, () -> adapter.execute(new RefreshResidentsViewsDtoRequest("OTRO")));
	}
}
