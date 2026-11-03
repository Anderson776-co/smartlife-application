package co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.residents.domain.ResidentsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.RefreshResidentsViewsDtoRequest;

@Mapper(componentModel = "spring")
public interface RefreshResidentsViewsMapperInput {

	ResidentsViewRefreshDomain toDomain(RefreshResidentsViewsDtoRequest request);

}
