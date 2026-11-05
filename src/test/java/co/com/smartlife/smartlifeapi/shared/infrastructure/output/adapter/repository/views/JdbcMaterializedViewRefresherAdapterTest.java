package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.repository.views;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcMaterializedViewRefresherAdapterTest {

	private JdbcTemplate jdbcTemplate;
	private JdbcMaterializedViewRefresherAdapter adapter;

	@BeforeEach
	void setUp() {
		jdbcTemplate = mock(JdbcTemplate.class);
		adapter = new JdbcMaterializedViewRefresherAdapter(jdbcTemplate);
	}

	@Test
	void refreshShouldRunTheConcurrentRefreshOfTheView() {
		adapter.refresh("visits.authorizator");

		verify(jdbcTemplate).execute("REFRESH MATERIALIZED VIEW CONCURRENTLY visits.authorizator");
	}

	@Test
	void refreshShouldRejectNamesThatCouldInjectSql() {
		for (final String name : new String[] { null, "", "sinesquema", "visits.authorizator; DROP TABLE x", "A.B", "a.b.c" }) {
			assertThrows(IllegalArgumentException.class, () -> adapter.refresh(name));
		}

		verifyNoInteractions(jdbcTemplate);
	}
}
