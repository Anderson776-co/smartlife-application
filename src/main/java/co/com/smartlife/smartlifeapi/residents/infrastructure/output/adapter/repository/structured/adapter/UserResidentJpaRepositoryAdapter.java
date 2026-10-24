package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.UserResidentJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.UserResidentJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.UserResidentMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.UserResidentRepository;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;

@Repository
public class UserResidentJpaRepositoryAdapter implements UserResidentRepository{
	
	private final UserResidentJpaRepository repository;
	private final UserResidentMapper mapper;

	public UserResidentJpaRepositoryAdapter(final UserResidentJpaRepository repository, final UserResidentMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Optional<UserResidentEntity> findById(UUID id) {
		Optional<UserResidentJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<UserResidentEntity> findAll() {
		List<UserResidentJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<UserResidentEntity> findByFilter(UserResidentEntity entity) {
		return List.of();
	}

}
