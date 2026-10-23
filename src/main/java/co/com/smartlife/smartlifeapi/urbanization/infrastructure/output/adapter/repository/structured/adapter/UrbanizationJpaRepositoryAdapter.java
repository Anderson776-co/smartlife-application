package co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.UrbanizationJpaRepository;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationJpaEntity;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.mapper.UrbanizationMapper;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.UrbanizationRepository;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

@Repository
public class UrbanizationJpaRepositoryAdapter implements UrbanizationRepository{
	
	private final UrbanizationJpaRepository repository;
	private final UrbanizationMapper mapper;
	
	

	public UrbanizationJpaRepositoryAdapter(final UrbanizationJpaRepository repository, final UrbanizationMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(UrbanizationEntity entity) {
		UrbanizationJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
		
	}

	@Override
	public Optional<UrbanizationEntity> findById(UUID id) {
		Optional<UrbanizationJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UrbanizationEntity> findAll() {
		List<UrbanizationJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<UrbanizationEntity> findByFilter(UrbanizationEntity entity) {
		return List.of();
	}
}
