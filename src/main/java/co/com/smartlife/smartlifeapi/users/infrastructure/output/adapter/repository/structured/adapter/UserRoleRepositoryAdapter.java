package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UserRoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserRoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserRoleMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRoleRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

@Repository
public class UserRoleRepositoryAdapter implements UserRoleRepository{
	
	private final UserRoleJpaRepository repository;
	private final UserRoleMapper mapper;
	
	public UserRoleRepositoryAdapter(UserRoleJpaRepository repository, UserRoleMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}
	@Override
	public void create(UserRoleEntity entity) {
		UserRoleJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}
	@Override
	public Optional<UserRoleEntity> findById(UUID id) {
		Optional<UserRoleJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}
	@Override
	public List<UserRoleEntity> findAll() {
		List<UserRoleJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}
	@Override
	public List<UserRoleEntity> findByFilter(UserRoleEntity entity) {
		return List.of();
	}
	@Override
	public void delete(UUID id, UserRoleEntity entity) {
		//Pendiente
	}
	@Override
	public List<UserRoleEntity> findByUserId(UUID userId) {
		List<UserRoleJpaEntity> entityJpaList = repository.findByUserId(userId);
		return mapper.toEntity(entityJpaList);
	}
	@Override
	public List<UserRoleEntity> findByRoleId(UUID roleId) {
		List<UserRoleJpaEntity> entityJpaList = repository.findByRoleId(roleId);
		return mapper.toEntity(entityJpaList);
	}
}
