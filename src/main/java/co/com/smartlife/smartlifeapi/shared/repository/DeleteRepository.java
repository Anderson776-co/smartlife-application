package co.com.smartlife.smartlifeapi.shared.repository;

public interface DeleteRepository <I, E>{
	void delete(I id, E entity);
}
