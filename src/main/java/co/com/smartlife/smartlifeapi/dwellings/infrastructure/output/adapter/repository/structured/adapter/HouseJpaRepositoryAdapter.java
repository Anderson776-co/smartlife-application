package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.HouseJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.HouseJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.HouseMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.HouseRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.HouseEntity;

@Repository
public class HouseJpaRepositoryAdapter implements HouseRepository{
	
	private final HouseJpaRepository repository;
	private final HouseMapper mapper;

	public HouseJpaRepositoryAdapter(final HouseJpaRepository repository, final HouseMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(HouseEntity entity) {
		HouseJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
		
	}

	@Override
	public Optional<HouseEntity> findById(UUID id) {
		Optional<HouseJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<HouseEntity> findAll() {
		List<HouseJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<HouseEntity> findByFilter(HouseEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, HouseEntity entity) {
		// Pendiente
		
	}

}
