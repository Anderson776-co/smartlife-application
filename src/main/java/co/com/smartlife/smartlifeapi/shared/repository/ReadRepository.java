package co.com.smartlife.smartlifeapi.shared.repository;

import java.util.List;
import java.util.Optional;

public interface ReadRepository <I, E>{
	Optional<E> findById(I id);
	
	List<E> findAll();
	
	List<E> findByFilter(E entity);
}
