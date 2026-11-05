package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.parameters.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.refresh.ContextRefresher;

class SpringParametersRefreshAdapterTest {

	@Test
	void shouldReturnTheKeysThatTheContextRefresherReports() {
		final ContextRefresher refresher = mock(ContextRefresher.class);
		when(refresher.refresh()).thenReturn(Set.of("smartlife.visits.max-anticipation"));

		final Set<String> changed = new SpringParametersRefreshAdapter(refresher).refresh();

		assertEquals(Set.of("smartlife.visits.max-anticipation"), changed);
	}
}
