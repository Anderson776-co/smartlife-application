package co.com.smartlife.smartlifeapi.shared.mapper;

import java.util.List;

public interface EntityMapper <E,J>{
	
	E toEntity(J jpa);
	
	J toJpaEntity(E entity);
	
	List<E> toEntity(List<J> entitiesJpa);

}
