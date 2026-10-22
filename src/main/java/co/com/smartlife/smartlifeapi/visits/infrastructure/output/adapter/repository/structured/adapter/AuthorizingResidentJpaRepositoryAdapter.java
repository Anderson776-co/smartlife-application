package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.AuthorizingResidentJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.AuthorizingResidentEntityJpa;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.AuthorizingResidentMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatingResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingResidentEntity;

@Repository
public class AuthorizingResidentJpaRepositoryAdapter implements AuthorizatingResidentRepository{
	
	private final AuthorizingResidentJpaRepository repository;
	private final AuthorizingResidentMapper mapper;
	
	public AuthorizingResidentJpaRepositoryAdapter(final AuthorizingResidentJpaRepository repository,
			final AuthorizingResidentMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public AuthorizingResidentEntity findById(UUID id) {
		AuthorizingResidentEntityJpa entityJpa = repository.findById(id).orElse(new AuthorizingResidentEntityJpa());
		return mapper.toEntity(entityJpa);
	}

	@Override
	public List<AuthorizingResidentEntity> findAll() {
		List<AuthorizingResidentEntityJpa> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<AuthorizingResidentEntity> findByFilter(AuthorizingResidentEntity entity) {
		return List.of();
	}


	

}
