package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

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

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitAuthorizationJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitAuthorizationMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

class VisitAuthorizationJpaRepositoryAdapterTest {

	private VisitAuthorizationJpaRepository repository;
	private VisitAuthorizationMapper mapper;
	private VisitAuthorizationJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(VisitAuthorizationJpaRepository.class);
		mapper = mock(VisitAuthorizationMapper.class);
		adapter = new VisitAuthorizationJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		VisitAuthorizationEntity entity = mock(VisitAuthorizationEntity.class);
		VisitAuthorizationJpaEntity jpaEntity = mock(VisitAuthorizationJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		VisitAuthorizationJpaEntity jpaEntity = mock(VisitAuthorizationJpaEntity.class);
		VisitAuthorizationEntity entity = mock(VisitAuthorizationEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitAuthorizationEntity> result = adapter.findById(id);

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
		List<VisitAuthorizationJpaEntity> jpaList = List.of(mock(VisitAuthorizationJpaEntity.class));
		List<VisitAuthorizationEntity> entities = List.of(mock(VisitAuthorizationEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(VisitAuthorizationEntity.class)).isEmpty());
	}

	@Test
	void updateShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.update(UUID.randomUUID(), mock(VisitAuthorizationEntity.class)));
	}
}