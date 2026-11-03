package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.views;

import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;

@Component
public class JdbcMaterializedViewRefresherAdapter implements MaterializedViewRefresherOutputPort {

	private static final Map<String, String> REFRESH_STATEMENTS = Map.of(
			"users.urbanization", "REFRESH MATERIALIZED VIEW CONCURRENTLY users.urbanization",
			"dwellings.urbanization", "REFRESH MATERIALIZED VIEW CONCURRENTLY dwellings.urbanization",
			"dwellings.user_owner", "REFRESH MATERIALIZED VIEW CONCURRENTLY dwellings.user_owner",
			"residents.user_resident", "REFRESH MATERIALIZED VIEW CONCURRENTLY residents.user_resident",
			"residents.residence", "REFRESH MATERIALIZED VIEW CONCURRENTLY residents.residence",
			"visits.authorizator", "REFRESH MATERIALIZED VIEW CONCURRENTLY visits.authorizator",
			"visits.property_visit", "REFRESH MATERIALIZED VIEW CONCURRENTLY visits.property_visit",
			"visits.resident", "REFRESH MATERIALIZED VIEW CONCURRENTLY visits.resident");

	private final JdbcTemplate jdbcTemplate;

	public JdbcMaterializedViewRefresherAdapter(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void refresh(final String viewName) {
		final String statement = Optional.ofNullable(REFRESH_STATEMENTS.get(TextHelper.getDefault(viewName)))
				.orElseThrow(() -> new IllegalArgumentException("Nombre de vista no valido: " + viewName));
		jdbcTemplate.execute(statement);
	}

}
