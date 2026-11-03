package co.com.smartlife.smartlifeapi.shared.service.output.port.views;

/** Pone al dia una vista materializada. El nombre va con el esquema, por ejemplo {@code visits.authorizator}. */
public interface MaterializedViewRefresherOutputPort {

	void refresh(String viewName);

}
