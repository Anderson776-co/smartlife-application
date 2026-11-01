package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.UserRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

@Repository
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
		UserJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);	
	}

	@Override
	public Optional<UserEntity> findById(UUID id) {
		Optional<UserJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UserEntity> findAll() {
		List<UserJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<UserEntity> findByFilter(UserEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, UserEntity entity) {
		UserJpaEntity entityJpa = repository.findById(id).orElseThrow(()-> new RuntimeException("Visitante no encontrado"));
		
		entityJpa.setFirstName(entity.getFirstName());
		entityJpa.setMiddleName(entity.getMiddleName());
		entityJpa.setLastName(entity.getLastName());
		entityJpa.setSecondLastName(entity.getSecondLastName());
		entityJpa.setDocumentNumber(entity.getDocumentNumber());
		entityJpa.setEmail(entity.getEmail());
		entityJpa.setEmailConfirmed(entity.getEmailConfirmed());
		entityJpa.setEmailConfirmedDefaultValue(entity.getEmailConfirmedDefaultValue());
		entityJpa.setIsActive(entity.getIsActive());
		
		repository.save(entityJpa);
	}

	@Override
	public Optional<UserEntity> findByEmail(String email) {
		Optional<UserJpaEntity> entityJpa = repository.findByEmail(email);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public Optional<UserEntity> findByDocumentNumber(String documentNumber) {
		Optional <UserJpaEntity> entityJpa = repository.findByDocumentNumber(documentNumber);
		return entityJpa.map(mapper::toEntity);
	}
	
	@Override
	public Optional<UserEntity> findByIdentityProviderUid(final String uid) {
		return repository.findByExternalId(uid).map(mapper::toEntity);
	}

}
