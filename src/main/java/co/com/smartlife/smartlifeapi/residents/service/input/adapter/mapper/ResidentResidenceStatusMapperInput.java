package co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.residents.domain.ResidentResidenceStatusChangeDomain;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.ChangeResidentResidenceStatusDtoRequest;

@Mapper(componentModel = "spring")
public interface ResidentResidenceStatusMapperInput {

	ResidentResidenceStatusChangeDomain toDomain(ChangeResidentResidenceStatusDtoRequest request);

}
