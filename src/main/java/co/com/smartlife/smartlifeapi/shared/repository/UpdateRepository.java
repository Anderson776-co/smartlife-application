package co.com.smartlife.smartlifeapi.shared.repository;

public interface UpdateRepository <I, E>{
	void update(I id, E entity);
}
