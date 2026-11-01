package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

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

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.HouseJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.HouseJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.HouseMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.HouseEntity;

class HouseJpaRepositoryAdapterTest {

	private HouseJpaRepository repository;
	private HouseMapper mapper;
	private HouseJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(HouseJpaRepository.class);
		mapper = mock(HouseMapper.class);
		adapter = new HouseJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		HouseEntity entity = mock(HouseEntity.class);
		HouseJpaEntity jpaEntity = mock(HouseJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		HouseJpaEntity jpaEntity = mock(HouseJpaEntity.class);
		HouseEntity entity = mock(HouseEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<HouseEntity> result = adapter.findById(id);

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
		List<HouseJpaEntity> jpaList = List.of(mock(HouseJpaEntity.class));
		List<HouseEntity> entities = List.of(mock(HouseEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(HouseEntity.class)).isEmpty());
	}

	@Test
	void updateShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.update(UUID.randomUUID(), mock(HouseEntity.class)));
	}
}