package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.UrbanizationDwellingJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationDwellingJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.UrbanizationDwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.UrbanizationDwellingRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UrbanizationDwellingEntity;

@Repository
public class UrbanizationDwellingJpaRepositoryAdapter implements UrbanizationDwellingRepository{
	
	private final UrbanizationDwellingJpaRepository repository;
	private final UrbanizationDwellingMapper mapper;

	public UrbanizationDwellingJpaRepositoryAdapter(final UrbanizationDwellingJpaRepository repository,
			final UrbanizationDwellingMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<UrbanizationDwellingEntity> findById(UUID id) {
		Optional<UrbanizationDwellingJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UrbanizationDwellingEntity> findAll() {
		List<UrbanizationDwellingJpaEntity> entityJpa = repository.findAll();
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<UrbanizationDwellingEntity> findByFilter(UrbanizationDwellingEntity entity) {
		return List.of();
	}

}
