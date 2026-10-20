package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.UrbanizationUserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UrbanizationUserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper.UrbanizationUserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UrbanizationUserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity.UrbanizationUserEntity;

@Repository
public class UrbanizationUserRepositoryAdapter implements UrbanizationUserRepository{
	
	private final UrbanizationUserJpaRepository repository;
	private final UrbanizationUserMapper mapper;
	
	

	public UrbanizationUserRepositoryAdapter(UrbanizationUserJpaRepository repository, UrbanizationUserMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public UrbanizationUserEntity findById(UUID id) {
		UrbanizationUserJpaEntity jpaEntity = repository.findById(id).orElse(null);
		return mapper.toEntity(jpaEntity);
	}

	@Override
	public List<UrbanizationUserEntity> findAll() {
		List<UrbanizationUserJpaEntity> jpaEntityList = repository.findAll();
		return mapper.toEntity(jpaEntityList);
	}

	@Override
	public List<UrbanizationUserEntity> findByFilter(UrbanizationUserEntity entity) {
		return List.of();
	}

}
