package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.UserOwnerJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UserOwnerJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.UserOwnerMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.UserOwnerRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UserOwnerEntity;

@Repository
public class UserOwnerJpaRepositoryAdapter implements UserOwnerRepository{
	
	private final UserOwnerJpaRepository repository;
	private final UserOwnerMapper mapper;
	
	

	public UserOwnerJpaRepositoryAdapter(final UserOwnerJpaRepository repository, final UserOwnerMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<UserOwnerEntity> findById(UUID id) {
		Optional<UserOwnerJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UserOwnerEntity> findAll() {
		List<UserOwnerJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<UserOwnerEntity> findByFilter(UserOwnerEntity entity) {
		return List.of();
	}

}
