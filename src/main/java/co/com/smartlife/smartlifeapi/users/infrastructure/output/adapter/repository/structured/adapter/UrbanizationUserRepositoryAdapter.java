package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UrbanizationUserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationUserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UrbanizationUserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UrbanizationUserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UrbanizationUserEntity;

@Repository
public class UrbanizationUserRepositoryAdapter implements UrbanizationUserRepository{
	
	private final UrbanizationUserJpaRepository repository;
	private final UrbanizationUserMapper mapper;
	
	

	public UrbanizationUserRepositoryAdapter(UrbanizationUserJpaRepository repository, UrbanizationUserMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<UrbanizationUserEntity> findById(UUID id) {
		Optional<UrbanizationUserJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UrbanizationUserEntity> findAll() {
		List<UrbanizationUserJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<UrbanizationUserEntity> findByFilter(UrbanizationUserEntity entity) {
		return List.of();
	}

}
