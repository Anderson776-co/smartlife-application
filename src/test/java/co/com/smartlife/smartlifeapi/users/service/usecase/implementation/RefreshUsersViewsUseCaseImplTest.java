package co.com.smartlife.smartlifeapi.users.service.usecase.implementation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.users.domain.UsersViewRefreshDomain;

class RefreshUsersViewsUseCaseImplTest {

	private MaterializedViewRefresherOutputPort refresher;
	private RefreshUsersViewsUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		refresher = mock(MaterializedViewRefresherOutputPort.class);
		useCase = new RefreshUsersViewsUseCaseImpl(refresher);
	}

	@Test
	void anUpdateFromUrbanizationShouldRefreshTheViewsThatDependOnIt() {
		useCase.execute(new UsersViewRefreshDomain(UsersViewRefreshDomain.UsersViewSource.URBANIZATION));

		verify(refresher).refresh("users.urbanization");
		verifyNoMoreInteractions(refresher);
	}

	@Test
	void aFailingRefreshShouldNotBreakTheCaller() {
		doThrow(new IllegalStateException("la base no responde")).when(refresher).refresh(anyString());

		useCase.execute(new UsersViewRefreshDomain(UsersViewRefreshDomain.UsersViewSource.URBANIZATION));

		verify(refresher).refresh("users.urbanization");
	}
}
