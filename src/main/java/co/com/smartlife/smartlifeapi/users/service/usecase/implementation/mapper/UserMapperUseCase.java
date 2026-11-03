package co.com.smartlife.smartlifeapi.users.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.users.domain.UserDomain;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

/** Dominio hacia entidad (para el repositorio) y entidad hacia dominio (lo que el repositorio devuelve). */
@Mapper(componentModel = "spring")
public interface UserMapperUseCase extends DomainMapper<UserDomain, UserEntity> {

	@Override
	@Mapping(target = "active", source = "isActive")
	UserDomain toDomain(UserEntity entity);

	@Override
	@Mapping(target = "isActive", source = "active")
	UserEntity toEntity(UserDomain domain);
}
