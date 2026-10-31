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

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitReasonJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitReasonJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitReasonMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

class VisitReasonJpaRepositoryAdapterTest {

	private VisitReasonJpaRepository repository;
	private VisitReasonMapper mapper;
	private VisitReasonJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(VisitReasonJpaRepository.class);
		mapper = mock(VisitReasonMapper.class);
		adapter = new VisitReasonJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		VisitReasonJpaEntity jpaEntity = mock(VisitReasonJpaEntity.class);
		VisitReasonEntity entity = mock(VisitReasonEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitReasonEntity> result = adapter.findById(id);

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
		List<VisitReasonJpaEntity> jpaList = List.of(mock(VisitReasonJpaEntity.class));
		List<VisitReasonEntity> entities = List.of(mock(VisitReasonEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(VisitReasonEntity.class)).isEmpty());
	}
}