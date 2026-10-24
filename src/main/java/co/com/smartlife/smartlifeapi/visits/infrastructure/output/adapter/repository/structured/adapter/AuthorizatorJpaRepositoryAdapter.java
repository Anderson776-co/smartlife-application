package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.AuthorizatorJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.AuthorizatorJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.AuthorizatorMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;

@Repository
public class AuthorizatorJpaRepositoryAdapter implements AuthorizatorRepository{
	
	private final AuthorizatorJpaRepository repository;
	private final AuthorizatorMapper mapper;
	
	

	public AuthorizatorJpaRepositoryAdapter(final AuthorizatorJpaRepository repository, final AuthorizatorMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<AuthorizatorEntity> findById(UUID id) {
		Optional<AuthorizatorJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<AuthorizatorEntity> findAll() {
		List<AuthorizatorJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<AuthorizatorEntity> findByFilter(AuthorizatorEntity entity) {
		return List.of();
	}

}
