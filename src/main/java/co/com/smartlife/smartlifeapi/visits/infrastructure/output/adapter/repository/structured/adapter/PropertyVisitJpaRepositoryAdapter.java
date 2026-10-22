package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.PropertyVisitJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.PropertyVisitEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.PropertyVisitMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;

public class PropertyVisitJpaRepositoryAdapter implements PropertyVisitRepository{
	
	private final PropertyVisitJpaRepository repository;
	private final PropertyVisitMapper mapper;
	
	

	public PropertyVisitJpaRepositoryAdapter(final PropertyVisitJpaRepository repository,
			final PropertyVisitMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}


	@Override
	public PropertyVisitEntity findById(UUID id) {
		PropertyVisitEntityJpa entityJpa = repository.findById(id).orElse(new PropertyVisitEntityJpa());
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<PropertyVisitEntity> findAll() {
		List<PropertyVisitEntityJpa> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<PropertyVisitEntity> findByFilter(PropertyVisitEntity entity) {
		return List.of();
	}
	
}
