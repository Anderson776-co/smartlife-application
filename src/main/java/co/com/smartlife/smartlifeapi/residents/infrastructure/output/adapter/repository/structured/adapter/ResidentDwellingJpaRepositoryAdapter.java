package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.ResidentResidenceJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidentResidenceJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidentResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.ResidentDwellingRepository;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;

@Repository
public class ResidentDwellingJpaRepositoryAdapter implements ResidentDwellingRepository{
	
	private final ResidentResidenceJpaRepository repository;
	private final ResidentResidenceMapper mapper;
	
	

	public ResidentDwellingJpaRepositoryAdapter(final ResidentResidenceJpaRepository repository,
			final ResidentResidenceMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void create(ResidentResidenceEntity entity) {
		ResidentResidenceJpaEntity entityJpa = mapper.toJpaEntity(entity);
		repository.save(entityJpa);
		
	}

	@Override
	public Optional<ResidentResidenceEntity> findById(UUID id) {
		return repository.findById(id)
	            .map(mapper::toEntity);
	}

	@Override
	public List<ResidentResidenceEntity> findAll() {
		List<ResidentResidenceJpaEntity> entityJpaList = repository.findAll();
		return mapper.toEntity(entityJpaList);
	}

	@Override
	public List<ResidentResidenceEntity> findByFilter(ResidentResidenceEntity entity) {
		return List.of();
	}

	@Override
	public void delete(UUID id, ResidentResidenceEntity entity) {
		//Pendiente
	}
	

}
