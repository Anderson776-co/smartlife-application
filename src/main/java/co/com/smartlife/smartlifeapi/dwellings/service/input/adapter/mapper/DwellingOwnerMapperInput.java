package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper;

import org.mapstruct.Mapper;

import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingOwnerChangeDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;

@Mapper(componentModel = "spring")
public interface DwellingOwnerMapperInput {

	DwellingOwnerChangeDomain toDomain(ChangeDwellingOwnerDtoRequest request);

}
