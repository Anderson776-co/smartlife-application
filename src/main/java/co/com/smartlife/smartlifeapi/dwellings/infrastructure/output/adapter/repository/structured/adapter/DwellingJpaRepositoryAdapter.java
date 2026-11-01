package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.DwellingJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.DwellingJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.DwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.DwellingRepository;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.DwellingEntity;

@Repository
public class DwellingJpaRepositoryAdapter implements DwellingRepository{
	
	private final DwellingJpaRepository repository;
	private final DwellingMapper mapper;
	
	

	public DwellingJpaRepositoryAdapter(final DwellingJpaRepository repository, final DwellingMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(DwellingEntity entity) {
		DwellingJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
	}

	@Override
	public Optional<DwellingEntity> findById(UUID id) {
		Optional<DwellingJpaEntity> entityJpa = repository.findById(id);
		return entityJpa.map(mapper::toEntity);
	}

	@Override
	public List<DwellingEntity> findAll() {
		List<DwellingJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<DwellingEntity> findByFilter(DwellingEntity entity) {
		return List.of();
	}

	@Override
	public void update(UUID id, DwellingEntity entity) {
		// Pendiente
		
	}

	

	
	

}
