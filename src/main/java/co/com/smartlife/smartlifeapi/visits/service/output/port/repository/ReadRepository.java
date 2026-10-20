package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.List;

public interface ReadRepository <I, E>{
	E findById(I id);
	
	List<E> findAll();
	
	List<E> findByFilter(E entity);
}
