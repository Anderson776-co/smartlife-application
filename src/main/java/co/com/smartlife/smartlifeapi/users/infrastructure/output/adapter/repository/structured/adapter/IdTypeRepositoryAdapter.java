package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.IdTypeJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.IdTypeJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.IdTypeMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.IdTypeRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;

@Repository
public class IdTypeRepositoryAdapter implements IdTypeRepository{
	
	private final IdTypeJpaRepository repository;
	private final IdTypeMapper mapper;
	
	

	public IdTypeRepositoryAdapter(IdTypeJpaRepository repository, IdTypeMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<IdTypeEntity> findById(UUID id) {
		Optional<IdTypeJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<IdTypeEntity> findAll() {
		List<IdTypeJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<IdTypeEntity> findByFilter(IdTypeEntity entity) {
		return List.of();
	}

}
