package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.UrbanizationDwellingJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationDwellingJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.UrbanizationDwellingMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UrbanizationDwellingEntity;

class UrbanizationDwellingJpaRepositoryAdapterTest {

	private UrbanizationDwellingJpaRepository repository;
	private UrbanizationDwellingMapper mapper;
	private UrbanizationDwellingJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UrbanizationDwellingJpaRepository.class);
		mapper = mock(UrbanizationDwellingMapper.class);
		adapter = new UrbanizationDwellingJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UrbanizationDwellingJpaEntity jpaEntity = mock(UrbanizationDwellingJpaEntity.class);
		UrbanizationDwellingEntity entity = mock(UrbanizationDwellingEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UrbanizationDwellingEntity> result = adapter.findById(id);

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
		List<UrbanizationDwellingJpaEntity> jpaList = List.of(mock(UrbanizationDwellingJpaEntity.class));
		List<UrbanizationDwellingEntity> entities = List.of(mock(UrbanizationDwellingEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UrbanizationDwellingEntity.class)).isEmpty());
	}
}