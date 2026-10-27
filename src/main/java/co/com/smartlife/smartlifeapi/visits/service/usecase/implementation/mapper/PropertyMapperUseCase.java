package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;

@Mapper(componentModel = "spring")
public interface PropertyMapperUseCase extends DomainMapper<PropertyVisitDomain, PropertyVisitEntity>{

}
