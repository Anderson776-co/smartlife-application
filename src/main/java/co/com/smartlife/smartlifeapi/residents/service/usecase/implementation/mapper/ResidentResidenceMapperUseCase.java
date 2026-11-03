package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceDomain;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;
import co.com.smartlife.smartlifeapi.residents.domain.UserResidentDomain;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;

/** Dominio hacia entidad (para el repositorio) y entidad hacia dominio (lo que el repositorio devuelve). */
@Mapper(componentModel = "spring")
public interface ResidentResidenceMapperUseCase extends DomainMapper<ResidentResidenceDomain, ResidentResidenceEntity> {

	@Override
	@Mapping(target = "active", source = "isActive")
	ResidentResidenceDomain toDomain(ResidentResidenceEntity entity);

	@Override
	@Mapping(target = "isActive", source = "active")
	ResidentResidenceEntity toEntity(ResidentResidenceDomain domain);

	/** Lo mismo para el usuario residente que viene anidado: isActive en la entidad, active en el dominio. */
	@Mapping(target = "active", source = "isActive")
	UserResidentDomain toUserResidentDomain(UserResidentEntity entity);

	@Mapping(target = "isActive", source = "active")
	UserResidentEntity toUserResidentEntity(UserResidentDomain domain);
}
