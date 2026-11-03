package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceDomain;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;
import co.com.smartlife.smartlifeapi.residents.domain.UserResidentDomain;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;

@Mapper(componentModel = "spring")
public interface ResidentResidenceMapperUseCase extends DomainMapper<ResidentResidenceDomain, ResidentResidenceEntity> {

	@Override
	@Mapping(target = "active", source = "isActive")
	ResidentResidenceDomain toDomain(ResidentResidenceEntity entity);

	@Override
	@Mapping(target = "isActive", source = "active")
	ResidentResidenceEntity toEntity(ResidentResidenceDomain domain);

	@Mapping(target = "active", source = "isActive")
	UserResidentDomain toUserResidentDomain(UserResidentEntity entity);

	@Mapping(target = "isActive", source = "active")
	UserResidentEntity toUserResidentEntity(UserResidentDomain domain);
}
