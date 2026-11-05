package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoResponseMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationPageDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationSummaryDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

@Mapper(componentModel = "spring")
public interface VisitAuthorizationSummaryInputMapper
		extends DtoResponseMapper<VisitAuthorizationSummaryDomain, VisitAuthorizationSummaryDtoResponse> {

	@Override
	@Mapping(target = "authorizationId", source = "id")
	VisitAuthorizationSummaryDtoResponse toDto(VisitAuthorizationSummaryDomain domain);

	VisitAuthorizationPageDtoResponse toPageDto(VisitAuthorizationPageDomain page);
}
