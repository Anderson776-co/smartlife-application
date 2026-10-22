package co.com.smartlife.smartlifeapi.shared.service.output.repository;

import java.util.List;

public interface ReadRepository <I, E>{
	E findById(I id);
	
	List<E> findAll();
	
	List<E> findByFilter(E entity);
}
