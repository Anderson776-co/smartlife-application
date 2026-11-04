package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;

@Mapper(componentModel = "spring", uses = VisitAuthorizationMapperInput.class)
public interface VisitAuthorizationAccessInputMapper {

	@Mapping(target = "authorizator", source = "requester")
	VisitAuthorizationAccessDomain toDomain(VisitAuthorizationAccessDtoRequest request);

}
