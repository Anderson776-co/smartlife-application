package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoResponseMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;

@Mapper(componentModel = "spring")
public interface PropertyVisitInputMapper extends DtoResponseMapper<PropertyVisitDomain, PropertyVisitDtoResponse> {

}
