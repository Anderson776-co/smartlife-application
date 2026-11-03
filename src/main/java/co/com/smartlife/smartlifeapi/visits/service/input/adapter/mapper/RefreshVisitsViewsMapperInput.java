package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.visits.domain.VisitsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.RefreshVisitsViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface RefreshVisitsViewsMapperInput {

	VisitsViewRefreshDomain toDomain(RefreshVisitsViewsDtoRequest request);

}
