package co.com.smartlife.smartlifeapi.shared.service.output.repository;

public interface UpdateRepository <I, E>{
	void update(I id, E entity);
}
