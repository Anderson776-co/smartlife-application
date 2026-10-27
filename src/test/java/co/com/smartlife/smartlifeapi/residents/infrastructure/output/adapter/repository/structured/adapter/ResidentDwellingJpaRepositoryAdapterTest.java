package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.ResidentResidenceJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidentResidenceJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidentResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;

class ResidentDwellingJpaRepositoryAdapterTest {

	private ResidentResidenceJpaRepository repository;
	private ResidentResidenceMapper mapper;
	private ResidentDwellingJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(ResidentResidenceJpaRepository.class);
		mapper = mock(ResidentResidenceMapper.class);
		adapter = new ResidentDwellingJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		ResidentResidenceEntity entity = mock(ResidentResidenceEntity.class);
		ResidentResidenceJpaEntity jpaEntity = mock(ResidentResidenceJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		ResidentResidenceJpaEntity jpaEntity = mock(ResidentResidenceJpaEntity.class);
		ResidentResidenceEntity entity = mock(ResidentResidenceEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<ResidentResidenceEntity> result = adapter.findById(id);

		assertTrue(result.isPresent());
		assertSame(entity, result.get());
	}

	@Test
	void findByIdShouldReturnEmptyWhenItDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertTrue(adapter.findById(id).isEmpty());
	}

	@Test
	void findAllShouldReturnTheMappedList() {
		List<ResidentResidenceJpaEntity> jpaList = List.of(mock(ResidentResidenceJpaEntity.class));
		List<ResidentResidenceEntity> entities = List.of(mock(ResidentResidenceEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(ResidentResidenceEntity.class)).isEmpty());
	}

	@Test
	void deleteShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.delete(UUID.randomUUID(), mock(ResidentResidenceEntity.class)));
	}
}