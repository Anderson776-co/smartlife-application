package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

public interface UpdateRepository <I, E>{
	void update(I id, E entity);
}
