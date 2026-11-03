package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.views;

import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;

/**
 * Refresca vistas materializadas de PostgreSQL. CONCURRENTLY deja leer la vista mientras se actualiza
 * (por eso cada vista tiene un indice unico). El nombre de la vista se valida porque se concatena en el SQL.
 */
@Component
public class JdbcMaterializedViewRefresherAdapter implements MaterializedViewRefresherOutputPort {

	private static final Pattern VIEW_NAME = Pattern.compile("[a-z_]+\\.[a-z_]+");

	private final JdbcTemplate jdbcTemplate;

	public JdbcMaterializedViewRefresherAdapter(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void refresh(final String viewName) {
		if (viewName == null || !VIEW_NAME.matcher(viewName).matches()) {
			throw new IllegalArgumentException("Nombre de vista no valido: " + viewName);
		}
		jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY " + viewName);
	}

}
