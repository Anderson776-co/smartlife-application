package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoResponseMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;

@Mapper(componentModel = "spring")
public interface VisitAuthorizationMapperResponseInput extends DtoResponseMapper<VisitAuthorizationDomain, VisitAuthorizationDtoResponse> {
	
	@Override
	@Mapping(target = "authorizationId", source = "id")
	@Mapping(target = "qrUrl", source = "qrCode")
	VisitAuthorizationDtoResponse toDto(VisitAuthorizationDomain domain);
}
