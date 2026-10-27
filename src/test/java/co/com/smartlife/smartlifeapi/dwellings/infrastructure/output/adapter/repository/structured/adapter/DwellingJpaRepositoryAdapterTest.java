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

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.DwellingJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.DwellingJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.DwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.DwellingEntity;

class DwellingJpaRepositoryAdapterTest {

	private DwellingJpaRepository repository;
	private DwellingMapper mapper;
	private DwellingJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(DwellingJpaRepository.class);
		mapper = mock(DwellingMapper.class);
		adapter = new DwellingJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		DwellingEntity entity = mock(DwellingEntity.class);
		DwellingJpaEntity jpaEntity = mock(DwellingJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		DwellingJpaEntity jpaEntity = mock(DwellingJpaEntity.class);
		DwellingEntity entity = mock(DwellingEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<DwellingEntity> result = adapter.findById(id);

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
		List<DwellingJpaEntity> jpaList = List.of(mock(DwellingJpaEntity.class));
		List<DwellingEntity> entities = List.of(mock(DwellingEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(DwellingEntity.class)).isEmpty());
	}

	@Test
	void updateShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.update(UUID.randomUUID(), mock(DwellingEntity.class)));
	}
}