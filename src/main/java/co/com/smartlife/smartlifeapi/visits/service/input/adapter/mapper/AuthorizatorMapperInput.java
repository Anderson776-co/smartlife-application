package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;

@Mapper(componentModel = "spring")
public interface AuthorizatorMapperInput extends DtoMapper<ListAuthorizatorPropertiesDtoRequest,AuthorizatorDomain>{

	@Mapping(target = "id", source = "currentUser.userId")
	@Mapping(target = "urbanizationId", source = "currentUser.urbanizationId")
	@Mapping(target = "roles", source = "currentUser.roles")
	AuthorizatorDomain toDomain(ListAuthorizatorPropertiesDtoRequest request);
	
}
