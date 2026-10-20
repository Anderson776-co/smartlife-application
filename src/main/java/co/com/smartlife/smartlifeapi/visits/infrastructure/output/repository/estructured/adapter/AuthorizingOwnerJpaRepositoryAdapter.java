package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.AuthorizingOwnerJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.AuthorizingOwnerEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.mapper.EntityMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatingOwnerRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingOwnerEntity;

@Repository
public class AuthorizingOwnerJpaRepositoryAdapter implements AuthorizatingOwnerRepository{
	
	private final AuthorizingOwnerJpaRepository repository;
	private final EntityMapper<AuthorizingOwnerEntity, AuthorizingOwnerEntityJpa> mapper;
	
	public AuthorizingOwnerJpaRepositoryAdapter(final AuthorizingOwnerJpaRepository repository,
			final EntityMapper<AuthorizingOwnerEntity, AuthorizingOwnerEntityJpa> mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public AuthorizingOwnerEntity findById(UUID id) {
		AuthorizingOwnerEntityJpa entityJpa = repository.findById(id).orElse(new AuthorizingOwnerEntityJpa());
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<AuthorizingOwnerEntity> findAll() {
		List<AuthorizingOwnerEntityJpa>entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<AuthorizingOwnerEntity> findByFilter(AuthorizingOwnerEntity entity) {
		return List.of();
	}

}
