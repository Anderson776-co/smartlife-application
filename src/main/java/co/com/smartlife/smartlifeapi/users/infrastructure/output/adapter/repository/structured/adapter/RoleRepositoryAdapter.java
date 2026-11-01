package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.RoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.RoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.RoleMapper;
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
	public Optional<RoleEntity> findById(UUID id) {
		Optional<RoleJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<RoleEntity> findAll() {
		List<RoleJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<RoleEntity> findByFilter(RoleEntity entity) {
		return List.of();
	}

}
