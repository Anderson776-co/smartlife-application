package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;

@Mapper(componentModel = "spring")
public interface VisitAuthorizationMapperInput extends DtoMapper<AuthorizeVisitDtoRequest, VisitAuthorizationDtoResponse, VisitAuthorizationDomain>{

}
