package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitResidentJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitResidentJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitResidentMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitResidentEntity;

class VisitResidentJpaRepositoryAdapterTest {

	private VisitResidentJpaRepository repository;
	private VisitResidentMapper mapper;
	private VisitResidentJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(VisitResidentJpaRepository.class);
		mapper = mock(VisitResidentMapper.class);
		adapter = new VisitResidentJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		VisitResidentJpaEntity jpaEntity = mock(VisitResidentJpaEntity.class);
		VisitResidentEntity entity = mock(VisitResidentEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitResidentEntity> result = adapter.findById(id);

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
		List<VisitResidentJpaEntity> jpaList = List.of(mock(VisitResidentJpaEntity.class));
		List<VisitResidentEntity> entities = List.of(mock(VisitResidentEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(VisitResidentEntity.class)).isEmpty());
	}
}