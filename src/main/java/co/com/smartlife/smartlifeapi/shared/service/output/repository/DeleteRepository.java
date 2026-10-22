package co.com.smartlife.smartlifeapi.shared.service.output.repository;

public interface DeleteRepository <I, E>{
	void delete(I id, E entity);
}
