package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

public interface UpdateRepository <ID, E>{
	void update(ID id, E entity);
}
