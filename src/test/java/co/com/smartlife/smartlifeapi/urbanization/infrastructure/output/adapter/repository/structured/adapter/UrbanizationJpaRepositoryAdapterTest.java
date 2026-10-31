package co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.adapter;

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

import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.UrbanizationJpaRepository;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationJpaEntity;
import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.mapper.UrbanizationMapper;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

class UrbanizationJpaRepositoryAdapterTest {

	private UrbanizationJpaRepository repository;
	private UrbanizationMapper mapper;
	private UrbanizationJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UrbanizationJpaRepository.class);
		mapper = mock(UrbanizationMapper.class);
		adapter = new UrbanizationJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		UrbanizationEntity entity = mock(UrbanizationEntity.class);
		UrbanizationJpaEntity jpaEntity = mock(UrbanizationJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UrbanizationJpaEntity jpaEntity = mock(UrbanizationJpaEntity.class);
		UrbanizationEntity entity = mock(UrbanizationEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UrbanizationEntity> result = adapter.findById(id);

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
		List<UrbanizationJpaEntity> jpaList = List.of(mock(UrbanizationJpaEntity.class));
		List<UrbanizationEntity> entities = List.of(mock(UrbanizationEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UrbanizationEntity.class)).isEmpty());
	}
}