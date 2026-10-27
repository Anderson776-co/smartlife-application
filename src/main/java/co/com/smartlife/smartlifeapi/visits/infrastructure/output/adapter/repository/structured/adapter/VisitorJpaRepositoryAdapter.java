package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitorJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitorJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitorMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

@Repository
public class VisitorJpaRepositoryAdapter implements VisitorRepository{
	
	private final VisitorJpaRepository repository;
	private final VisitorMapper mapper;

	public VisitorJpaRepositoryAdapter(VisitorJpaRepository repository,
			VisitorMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(VisitorEntity entity) {
		VisitorJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

	@Override
	public Optional<VisitorEntity> findById(UUID id) {
		Optional<VisitorJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<VisitorEntity> findAll() {
		List<VisitorJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<VisitorEntity> findByFilter(VisitorEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, VisitorEntity entity) {
		//pendiente
	}

	@Override
	public Optional<VisitorEntity> findByEmail(String email) {
		Optional<VisitorJpaEntity> entityJpa = repository.findByEmail(email);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public Optional<VisitorEntity> findByDocumentNumber(String documentNumber) {
		Optional<VisitorJpaEntity> entityJpa = repository.findByDocumentNumber(documentNumber);
		return entityJpa.map(mapper::toEntity);
	}

}
