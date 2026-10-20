package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.UserRoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserRoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper.UserRoleMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRoleRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity.UserRoleEntity;

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
		UserRoleJpaEntity jpaEntity = mapper.toJpaEntity(entity);
		repository.save(jpaEntity);
	}
	@Override
	public UserRoleEntity findById(UUID id) {
		UserRoleJpaEntity jpaEntity = repository.findById(id).orElse(null);
		return mapper.toEntity(jpaEntity);
	}
	@Override
	public List<UserRoleEntity> findAll() {
		List<UserRoleJpaEntity> jpaEntityList = repository.findAll();
		return mapper.toEntity(jpaEntityList);
	}
	@Override
	public List<UserRoleEntity> findByFilter(UserRoleEntity entity) {
		return List.of();
	}
	@Override
	public void delete(UUID id, UserRoleEntity entity) {
		UserRoleJpaEntity jpaEntity = repository.findById(id).orElse(null);
		repository.delete(jpaEntity);
	}
	@Override
	public Optional<UserRoleEntity> findByUserId(UUID userId) {
		Optional<UserRoleJpaEntity> jpaEntity = repository.findByUserId(userId);
		return jpaEntity.map(mapper::toEntity);
	}
	@Override
	public Optional<UserRoleEntity> findByRoleId(UUID roleId) {
		Optional<UserRoleJpaEntity> jpaEntity = repository.findByRoleId(roleId);
		return jpaEntity.map(mapper::toEntity);
	}
	
	
	


}
