package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentsViewRefreshDomain;

class RefreshResidentsViewsUseCaseImplTest {

	private MaterializedViewRefresherOutputPort refresher;
	private RefreshResidentsViewsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		refresher = mock(MaterializedViewRefresherOutputPort.class);
		useCase = new RefreshResidentsViewsUseCaseImpl(refresher);
	}

	@Test
	void anUpdateFromUsersShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new ResidentsViewRefreshDomain(ResidentsViewRefreshDomain.ResidentsViewSource.USERS));

		verify(refresher).refresh("residents.user_resident");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void anUpdateFromDwellingsShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new ResidentsViewRefreshDomain(ResidentsViewRefreshDomain.ResidentsViewSource.DWELLINGS));

		verify(refresher).refresh("residents.residence");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void aFailingRefreshShouldNotBreakTheCaller() {
		doThrow(new IllegalStateException("la base no responde")).when(refresher).refresh(anyString());

		useCase.execute(new ResidentsViewRefreshDomain(ResidentsViewRefreshDomain.ResidentsViewSource.USERS));

		verify(refresher).refresh("residents.user_resident");
	}
}
