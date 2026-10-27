package co.com.smartlife.smartlifeapi.shared.mapper;

public interface DomainMapper <D,E>{
	
	D toDomain(E entity);
	
	E toEntity(D domain);

}
