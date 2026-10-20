package co.com.smartlife.smartlifeapi.shared.services.output.repository;

public interface DeleteRepository <I, E>{
	void delete(I id, E entity);
}
