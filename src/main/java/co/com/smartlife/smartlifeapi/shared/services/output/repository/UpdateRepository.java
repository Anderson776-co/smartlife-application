package co.com.smartlife.smartlifeapi.shared.services.output.repository;

public interface UpdateRepository <I, E>{
	void update(I id, E entity);
}
