package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;

@Mapper(componentModel = "spring")
public interface AuthorizatorMapperUseCase extends DomainMapper<AuthorizatorDomain, AuthorizatorEntity>{

}
