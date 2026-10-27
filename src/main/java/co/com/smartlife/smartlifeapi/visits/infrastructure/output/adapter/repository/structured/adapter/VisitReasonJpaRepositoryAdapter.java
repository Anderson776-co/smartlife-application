package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitReasonJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitReasonJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitReasonMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

@Repository
public class VisitReasonJpaRepositoryAdapter implements VisitReasonRepository{
	
	private final VisitReasonJpaRepository repository;
	private final VisitReasonMapper mapper;
	
	

	public VisitReasonJpaRepositoryAdapter(final VisitReasonJpaRepository repository,
			final VisitReasonMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<VisitReasonEntity> findById(UUID id) {
		Optional<VisitReasonJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<VisitReasonEntity> findAll() {
		List<VisitReasonJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<VisitReasonEntity> findByFilter(VisitReasonEntity entity) {
		return List.of();
	}

}
