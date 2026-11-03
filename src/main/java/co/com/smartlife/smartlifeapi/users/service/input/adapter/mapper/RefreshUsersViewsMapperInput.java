package co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.users.domain.UsersViewRefreshDomain;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.RefreshUsersViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface RefreshUsersViewsMapperInput {

	UsersViewRefreshDomain toDomain(RefreshUsersViewsDtoRequest request);

}
