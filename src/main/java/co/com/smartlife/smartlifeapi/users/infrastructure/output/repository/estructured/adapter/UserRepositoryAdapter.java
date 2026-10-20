package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.UserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.mapper.UserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

public class UserRepositoryAdapter implements UserRepository {
	
	private final UserJpaRepository repository;
	private final UserMapper mapper;
	
	

	public UserRepositoryAdapter(UserJpaRepository repository, UserMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(UserEntity entity) {
		UserJpaEntity jpaEntity = mapper.toJpaEntity(entity);
		repository.save(jpaEntity);	
	}

	@Override
	public UserEntity findById(UUID id) {
		UserJpaEntity jpaEntity = repository.findById(id).orElse(null);
		return mapper.toEntity(jpaEntity);
	}

	@Override
	public List<UserEntity> findAll() {
		List<UserJpaEntity> jpaEntityList = repository.findAll();
		return mapper.toEntity(jpaEntityList);
	}

	@Override
	public List<UserEntity> findByFilter(UserEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, UserEntity entity) {
		UserJpaEntity jpaEntity = repository.findById(id).orElseThrow(()-> new RuntimeException("Visitante no encontrado"));
		
		jpaEntity.setFirstName(entity.getFirstName());
	    jpaEntity.setMiddleName(entity.getMiddleName());
	    jpaEntity.setLastName(entity.getLastName());
	    jpaEntity.setSecondLastName(entity.getSecondLastName());
	    jpaEntity.setDocumentNumber(entity.getDocumentNumber());
	    jpaEntity.setEmail(entity.getEmail());
	    jpaEntity.setIsEmailConfirmed(entity.getIsEmailConfirmed());
	    jpaEntity.setIsEmailConfirmedDefaultValue(entity.getIsEmailConfirmedDefaultValue());
	    jpaEntity.setIsActive(entity.getIsActive());
	}

	@Override
	public Optional<UserEntity> findByEmail(String email) {
		Optional<UserJpaEntity> jpaEntity = repository.findByEmail(email);
		return jpaEntity.map(mapper::toEntity);
	}

	@Override
	public Optional<UserEntity> findByDocumentNumber(String documentNumber) {
		Optional <UserJpaEntity> jpaEntity = repository.findByDocumentNumber(documentNumber);
		return jpaEntity.map(mapper::toEntity);
	}

}
