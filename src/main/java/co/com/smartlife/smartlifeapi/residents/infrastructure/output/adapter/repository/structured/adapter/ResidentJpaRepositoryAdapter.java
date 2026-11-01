package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.ResidentJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidentJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidentMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.ResidentRepository;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentEntity;

@Repository
public class ResidentJpaRepositoryAdapter implements ResidentRepository {
	
	private final ResidentJpaRepository repository;
	private final ResidentMapper mapper;

	public ResidentJpaRepositoryAdapter(final ResidentJpaRepository repository, final ResidentMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(ResidentEntity entity) {
		ResidentJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

	@Override
	public Optional<ResidentEntity> findById(UUID id) {
		Optional<ResidentJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<ResidentEntity> findAll() {
		List<ResidentJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<ResidentEntity> findByFilter(ResidentEntity entity) {
		return List.of();
	}

}
