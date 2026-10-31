package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

@Mapper(componentModel = "spring", uses = { PropertyVisitMapper.class, AuthorizatorMapper.class })
public interface VisitAuthorizationMapper extends EntityMapper<VisitAuthorizationEntity, VisitAuthorizationJpaEntity>{

}
