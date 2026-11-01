package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.ResidenceJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidenceJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.ResidenceRepository;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidenceEntity;

@Repository
public class ResidenceJpaRepositoryAdapter implements ResidenceRepository{
	
	private final ResidenceJpaRepository repository;
	private final ResidenceMapper mapper;
	
	public ResidenceJpaRepositoryAdapter(final ResidenceJpaRepository repository, final ResidenceMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<ResidenceEntity> findById(UUID id) {
		Optional<ResidenceJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<ResidenceEntity> findAll() {
		List<ResidenceJpaEntity> entityJpa = repository.findAll();
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<ResidenceEntity> findByFilter(ResidenceEntity entity) {
		return List.of();
	}

}
