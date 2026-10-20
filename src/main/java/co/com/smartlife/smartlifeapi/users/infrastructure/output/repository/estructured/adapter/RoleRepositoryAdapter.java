package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.RoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.RoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper.RoleMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.RoleRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.RoleEntity;

@Repository
public class RoleRepositoryAdapter implements RoleRepository{
	
	private final RoleJpaRepository repository;
	private final RoleMapper mapper;
	
	

	public RoleRepositoryAdapter(RoleJpaRepository repository, RoleMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public RoleEntity findById(UUID id) {
		RoleJpaEntity jpaEntity = repository.findById(id).orElse(null);
		return mapper.toEntity(jpaEntity);
	}

	@Override
	public List<RoleEntity> findAll() {
		List<RoleJpaEntity> jpaEntityList = repository.findAll();
		return mapper.toEntity(jpaEntityList);
	}

	@Override
	public List<RoleEntity> findByFilter(RoleEntity entity) {
		return List.of();
	}

}
