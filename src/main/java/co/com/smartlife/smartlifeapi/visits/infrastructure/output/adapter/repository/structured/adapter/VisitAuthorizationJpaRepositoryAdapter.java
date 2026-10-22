package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitAuthorizationJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitAuthorizationMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

@Repository
public class VisitAuthorizationJpaRepositoryAdapter implements VisitAuthorizationRepository{
	
	private final VisitAuthorizationJpaRepository repository;
	private final VisitAuthorizationMapper mapper;
	
	

	public VisitAuthorizationJpaRepositoryAdapter(final VisitAuthorizationJpaRepository repository,
			final VisitAuthorizationMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(VisitAuthorizationEntity entity) {
		VisitAuthorizationEntityJpa entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

	@Override
	public VisitAuthorizationEntity findById(UUID id) {
		VisitAuthorizationEntityJpa entityJpa = repository.findById(id).orElse(new VisitAuthorizationEntityJpa());
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<VisitAuthorizationEntity> findAll() {
		List<VisitAuthorizationEntityJpa> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<VisitAuthorizationEntity> findByFilter(VisitAuthorizationEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, VisitAuthorizationEntity entity) {
		VisitAuthorizationEntityJpa entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

}
