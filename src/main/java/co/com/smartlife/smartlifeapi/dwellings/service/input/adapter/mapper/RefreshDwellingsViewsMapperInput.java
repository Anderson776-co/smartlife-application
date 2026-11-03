package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.RefreshDwellingsViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface RefreshDwellingsViewsMapperInput {

	DwellingsViewRefreshDomain toDomain(RefreshDwellingsViewsDtoRequest request);

}
