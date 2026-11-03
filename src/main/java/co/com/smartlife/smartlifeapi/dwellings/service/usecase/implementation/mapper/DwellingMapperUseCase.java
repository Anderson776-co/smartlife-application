package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.DwellingEntity;
import co.com.smartlife.smartlifeapi.dwellings.domain.UserOwnerDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UserOwnerEntity;

@Mapper(componentModel = "spring")
public interface DwellingMapperUseCase extends DomainMapper<DwellingDomain, DwellingEntity> {

	@Mapping(target = "active", source = "isActive")
	UserOwnerDomain toOwnerDomain(UserOwnerEntity entity);

	@Mapping(target = "isActive", source = "active")
	UserOwnerEntity toOwnerEntity(UserOwnerDomain domain);
}
