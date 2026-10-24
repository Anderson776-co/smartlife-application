package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.ResidenceJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidenceJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.ResidenceMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidenceEntity;

class ResidenceJpaRepositoryAdapterTest {

	private ResidenceJpaRepository repository;
	private ResidenceMapper mapper;
	private ResidenceJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(ResidenceJpaRepository.class);
		mapper = mock(ResidenceMapper.class);
		adapter = new ResidenceJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		ResidenceJpaEntity jpaEntity = mock(ResidenceJpaEntity.class);
		ResidenceEntity entity = mock(ResidenceEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<ResidenceEntity> result = adapter.findById(id);

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
		List<ResidenceJpaEntity> jpaList = List.of(mock(ResidenceJpaEntity.class));
		List<ResidenceEntity> entities = List.of(mock(ResidenceEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(ResidenceEntity.class)).isEmpty());
	}
}