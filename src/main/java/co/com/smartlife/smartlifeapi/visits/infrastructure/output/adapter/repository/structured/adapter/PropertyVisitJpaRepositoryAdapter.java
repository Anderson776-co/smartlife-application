package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.PropertyVisitJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.PropertyVisitJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.PropertyVisitMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;

@Repository
public class PropertyVisitJpaRepositoryAdapter implements PropertyVisitRepository{
	
	private final PropertyVisitJpaRepository repository;
	private final PropertyVisitMapper mapper;

	public PropertyVisitJpaRepositoryAdapter(final PropertyVisitJpaRepository repository,
			final PropertyVisitMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}


	@Override
	public Optional<PropertyVisitEntity> findById(UUID id) {
		Optional<PropertyVisitJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<PropertyVisitEntity> findAll() {
		List<PropertyVisitJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<PropertyVisitEntity> findByFilter(PropertyVisitEntity entity) {
		return List.of();
	}

	@Override
	public boolean isOwnedBy(final UUID propertyId, final UUID ownerId) {
		return repository.existsByIdAndOwnerId(propertyId, ownerId);
	}
	
}
