package co.com.smartlife.smartlifeapi.shared.mapper;

public interface DtoMapper<I, D> {
    D toDomain(I dto);
}
