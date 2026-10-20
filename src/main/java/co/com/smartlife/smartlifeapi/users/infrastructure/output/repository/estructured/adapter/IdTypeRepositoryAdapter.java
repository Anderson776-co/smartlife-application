package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.IdTypeJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.IdTypeJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper.IdTypeMapper;
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
	public IdTypeEntity findById(UUID id) {
		IdTypeJpaEntity jpaEntity = repository.findById(id).orElse(null);
		return mapper.toEntity(jpaEntity);
	}

	@Override
	public List<IdTypeEntity> findAll() {
		List<IdTypeJpaEntity> jpaEntityList = repository.findAll();
		return mapper.toEntity(jpaEntityList);
	}

	@Override
	public List<IdTypeEntity> findByFilter(IdTypeEntity entity) {
		return List.of();
	}

}
