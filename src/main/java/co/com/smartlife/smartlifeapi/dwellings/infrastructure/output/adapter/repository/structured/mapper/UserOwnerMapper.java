package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UserOwnerJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UserOwnerEntity;
import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;

@Mapper(componentModel = "spring")
public interface UserOwnerMapper extends EntityMapper<UserOwnerEntity, UserOwnerJpaEntity>{

}
