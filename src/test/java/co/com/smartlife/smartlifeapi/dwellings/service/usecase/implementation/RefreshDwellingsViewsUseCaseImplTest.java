package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingsViewRefreshDomain;

class RefreshDwellingsViewsUseCaseImplTest {

	private MaterializedViewRefresherOutputPort refresher;
	private RefreshDwellingsViewsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		refresher = mock(MaterializedViewRefresherOutputPort.class);
		useCase = new RefreshDwellingsViewsUseCaseImpl(refresher);
	}

	@Test
	void anUpdateFromUsersShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new DwellingsViewRefreshDomain(DwellingsViewRefreshDomain.DwellingsViewSource.USERS));

		verify(refresher).refresh("dwellings.user_owner");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void anUpdateFromUrbanizationShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new DwellingsViewRefreshDomain(DwellingsViewRefreshDomain.DwellingsViewSource.URBANIZATION));

		verify(refresher).refresh("dwellings.urbanization");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void aFailingRefreshShouldNotBreakTheCaller() {
		doThrow(new IllegalStateException("la base no responde")).when(refresher).refresh(anyString());

		useCase.execute(new DwellingsViewRefreshDomain(DwellingsViewRefreshDomain.DwellingsViewSource.USERS));

		verify(refresher).refresh("dwellings.user_owner");
	}
}
