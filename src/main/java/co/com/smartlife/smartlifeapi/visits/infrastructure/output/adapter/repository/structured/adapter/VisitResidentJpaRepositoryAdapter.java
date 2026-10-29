package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitResidentJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitResidentJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitResidentMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitResidentEntity;

@Repository
public class VisitResidentJpaRepositoryAdapter implements VisitResidentRepository{
	
	private final VisitResidentJpaRepository repository;
	private final VisitResidentMapper mapper;
	

	public VisitResidentJpaRepositoryAdapter(final VisitResidentJpaRepository repository, final VisitResidentMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<VisitResidentEntity> findById(UUID id) {
		Optional<VisitResidentJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<VisitResidentEntity> findAll() {
		List<VisitResidentJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<VisitResidentEntity> findByFilter(VisitResidentEntity entity) {
		return List.of();
	}

	@Override
	public boolean residesIn(final UUID residentId, final UUID propertyId) {
		return repository.existsByIdAndPropertyId(residentId, propertyId);
	}

}
