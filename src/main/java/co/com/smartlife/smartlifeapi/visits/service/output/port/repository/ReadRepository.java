package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.List;

public interface ReadRepository <ID, E>{
	E findById(ID id);
	
	List<E> findAll();
	
	List<E> findByFilter(E entity);
}
