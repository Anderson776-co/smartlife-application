package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.views;

import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;

@Component
public class JdbcMaterializedViewRefresherAdapter implements MaterializedViewRefresherOutputPort {

	private static final Pattern VIEW_NAME = Pattern.compile("[a-z_]+\\.[a-z_]+");

	private final JdbcTemplate jdbcTemplate;

	public JdbcMaterializedViewRefresherAdapter(final JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void refresh(final String viewName) {
		if (ObjectHelper.isNull(viewName) || !VIEW_NAME.matcher(viewName).matches()) {
			throw new IllegalArgumentException("Nombre de vista no valido: " + viewName);
		}
		jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY " + viewName);
	}

}
