package co.com.smartlife.smartlifeapi.shared.mapper;

import java.util.List;

public interface DtoMapper <T,D>{
	
	T toDTO(D domain);
	
	D toDomain(T dto);
	
	List<T> toDTO(List<D> domainList);

}
