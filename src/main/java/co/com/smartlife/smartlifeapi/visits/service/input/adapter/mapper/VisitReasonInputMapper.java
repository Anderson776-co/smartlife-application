package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoResponseMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;

@Mapper(componentModel = "spring")
public interface VisitReasonInputMapper extends DtoResponseMapper<VisitReasonDomain, VisitReasonDtoResponse> {

}
