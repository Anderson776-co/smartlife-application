package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.ApartmentJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.ApartmentJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.ApartmentMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.ApartmentRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.ApartmentEntity;

@Repository
public class ApartmentJpaRepositoryAdapter implements ApartmentRepository{
	
	private final ApartmentJpaRepository repository;
	private final ApartmentMapper mapper;

	public ApartmentJpaRepositoryAdapter(final ApartmentJpaRepository repository, final ApartmentMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(ApartmentEntity entity) {
		ApartmentJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

	@Override
	public Optional<ApartmentEntity> findById(UUID id) {
		Optional<ApartmentJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<ApartmentEntity> findAll() {
		List<ApartmentJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<ApartmentEntity> findByFilter(ApartmentEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, ApartmentEntity entity) {
		//Pendiente
	}

}
