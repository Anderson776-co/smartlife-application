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

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.VisitorJpaRepository;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitorJpaEntity;
import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.mapper.VisitorMapper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

class VisitorJpaRepositoryAdapterTest {

	private VisitorJpaRepository repository;
	private VisitorMapper mapper;
	private VisitorJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(VisitorJpaRepository.class);
		mapper = mock(VisitorMapper.class);
		adapter = new VisitorJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		VisitorEntity entity = mock(VisitorEntity.class);
		VisitorJpaEntity jpaEntity = mock(VisitorJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		VisitorJpaEntity jpaEntity = mock(VisitorJpaEntity.class);
		VisitorEntity entity = mock(VisitorEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitorEntity> result = adapter.findById(id);

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
		List<VisitorJpaEntity> jpaList = List.of(mock(VisitorJpaEntity.class));
		List<VisitorEntity> entities = List.of(mock(VisitorEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(VisitorEntity.class)).isEmpty());
	}

	@Test
	void updateShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.update(UUID.randomUUID(), mock(VisitorEntity.class)));
	}

	@Test
	void findByEmailShouldReturnTheMappedEntityWhenItExists() {
		VisitorJpaEntity jpaEntity = mock(VisitorJpaEntity.class);
		VisitorEntity entity = mock(VisitorEntity.class);
		when(repository.findByEmail("visitante@test.com")).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitorEntity> result = adapter.findByEmail("visitante@test.com");

		assertTrue(result.isPresent());
		assertSame(entity, result.get());
	}

	@Test
	void findByEmailShouldReturnEmptyWhenItDoesNotExist() {
		when(repository.findByEmail("nadie@test.com")).thenReturn(Optional.empty());

		assertTrue(adapter.findByEmail("nadie@test.com").isEmpty());
	}

	@Test
	void findByDocumentNumberShouldReturnTheMappedEntityWhenItExists() {
		VisitorJpaEntity jpaEntity = mock(VisitorJpaEntity.class);
		VisitorEntity entity = mock(VisitorEntity.class);
		when(repository.findByDocumentNumber("123456789")).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitorEntity> result = adapter.findByDocumentNumber("123456789");

		assertTrue(result.isPresent());
		assertSame(entity, result.get());
	}

	@Test
	void findByDocumentNumberShouldReturnEmptyWhenItDoesNotExist() {
		when(repository.findByDocumentNumber("000")).thenReturn(Optional.empty());

		assertTrue(adapter.findByDocumentNumber("000").isEmpty());
	}
}