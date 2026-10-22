package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.AuthorizingOwnerJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.AuthorizingOwnerEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.AuthorizingOwnerMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatingOwnerRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingOwnerEntity;

@Repository
public class AuthorizingOwnerJpaRepositoryAdapter implements AuthorizatingOwnerRepository{
	
	private final AuthorizingOwnerJpaRepository repository;
	private final AuthorizingOwnerMapper mapper;
	
	public AuthorizingOwnerJpaRepositoryAdapter(final AuthorizingOwnerJpaRepository repository,
			final AuthorizingOwnerMapper mapper) {
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
