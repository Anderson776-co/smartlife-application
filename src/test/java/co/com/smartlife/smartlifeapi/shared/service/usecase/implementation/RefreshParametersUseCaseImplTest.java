package co.com.smartlife.smartlifeapi.shared.service.usecase.implementation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeDomain;
import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeEventDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.parameters.ParametersRefreshOutputPort;

class RefreshParametersUseCaseImplTest {

	private static final String MAIN = "refs/heads/main";

	private ParametersRefreshOutputPort parametersRefresh;
	private RefreshParametersUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		parametersRefresh = mock(ParametersRefreshOutputPort.class);
		useCase = new RefreshParametersUseCaseImpl(parametersRefresh, MAIN);
	}

	@Test
	void shouldRefreshOnAPushToTheWatchedBranch() {
		when(parametersRefresh.refresh()).thenReturn(Set.of("smartlife.visits.max-anticipation"));

		useCase.execute(new ParametersChangeDomain(ParametersChangeEventDomain.PUSH, MAIN));

		verify(parametersRefresh).refresh();
	}

	@Test
	void shouldIgnoreAPushToAnotherBranch() {
		useCase.execute(new ParametersChangeDomain(ParametersChangeEventDomain.PUSH, "refs/heads/otra"));

		verifyNoInteractions(parametersRefresh);
	}

	@Test
	void shouldIgnoreEventsThatAreNotAPush() {
		useCase.execute(new ParametersChangeDomain(ParametersChangeEventDomain.OTHER, ""));

		verifyNoInteractions(parametersRefresh);
	}
}
