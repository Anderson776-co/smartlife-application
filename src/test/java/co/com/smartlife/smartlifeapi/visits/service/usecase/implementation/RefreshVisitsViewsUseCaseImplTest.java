package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.visits.domain.VisitsViewRefreshDomain;

class RefreshVisitsViewsUseCaseImplTest {

	private MaterializedViewRefresherOutputPort refresher;
	private RefreshVisitsViewsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		refresher = mock(MaterializedViewRefresherOutputPort.class);
		useCase = new RefreshVisitsViewsUseCaseImpl(refresher);
	}

	@Test
	void anUpdateFromUsersShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new VisitsViewRefreshDomain(VisitsViewRefreshDomain.VisitsViewSource.USERS));

		verify(refresher).refresh("visits.authorizator");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void anUpdateFromDwellingsShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new VisitsViewRefreshDomain(VisitsViewRefreshDomain.VisitsViewSource.DWELLINGS));

		verify(refresher).refresh("visits.property_visit");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void anUpdateFromResidentsShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new VisitsViewRefreshDomain(VisitsViewRefreshDomain.VisitsViewSource.RESIDENTS));

		verify(refresher).refresh("visits.resident");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void aFailingRefreshShouldNotBreakTheCaller() {
		doThrow(new IllegalStateException("la base no responde")).when(refresher).refresh(anyString());

		useCase.execute(new VisitsViewRefreshDomain(VisitsViewRefreshDomain.VisitsViewSource.USERS));

		verify(refresher).refresh("visits.authorizator");
	}
}
