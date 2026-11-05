package co.com.smartlife.smartlifeapi.shared.mapper;

import java.util.List;

public interface DtoResponseMapper <D,R> {
	
	R toDto(D domain);
	
	List<R> toDtoList(List<D> domainList);

}
