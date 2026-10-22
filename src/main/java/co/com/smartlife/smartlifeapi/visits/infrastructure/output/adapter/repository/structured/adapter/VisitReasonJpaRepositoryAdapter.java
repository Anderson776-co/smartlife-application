package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.VisitReasonJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.VisitReasonEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.mapper.VisitReasonMapper;
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
	public VisitReasonEntity findById(UUID id) {
		VisitReasonEntityJpa entityJpa = repository.findById(id).orElse(new VisitReasonEntityJpa());
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<VisitReasonEntity> findAll() {
		List<VisitReasonEntityJpa> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<VisitReasonEntity> findByFilter(VisitReasonEntity entity) {
		return List.of();
	}

}
