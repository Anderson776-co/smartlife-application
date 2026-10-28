package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

@Mapper(componentModel = "spring")
public interface VisitReasonMapperUseCase extends DomainMapper<VisitReasonDomain, VisitReasonEntity>{

}
