package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;

@Mapper(componentModel = "spring")
public interface VisitAuthorizationFilterMapperInput
		extends DtoMapper<ListVisitAuthorizationsDtoRequest, VisitAuthorizationFilterDomain> {

	@Override
	@Mapping(target = "authorizatorId", source = "currentUser.userId")
	VisitAuthorizationFilterDomain toDomain(ListVisitAuthorizationsDtoRequest request);
}
