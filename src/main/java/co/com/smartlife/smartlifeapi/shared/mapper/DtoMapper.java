package co.com.smartlife.smartlifeapi.shared.mapper;

import java.util.List;

public interface DtoMapper<I, O, D> {

    O toDTO(D domain);

    D toDomain(I dto);

    List<O> toDTO(List<D> domainList);
}
