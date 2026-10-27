package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.IdTypeJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.IdTypeJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.IdTypeMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;

class IdTypeRepositoryAdapterTest {

	private IdTypeJpaRepository repository;
	private IdTypeMapper mapper;
	private IdTypeRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(IdTypeJpaRepository.class);
		mapper = mock(IdTypeMapper.class);
		adapter = new IdTypeRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		IdTypeJpaEntity jpaEntity = mock(IdTypeJpaEntity.class);
		IdTypeEntity entity = mock(IdTypeEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<IdTypeEntity> result = adapter.findById(id);

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
		List<IdTypeJpaEntity> jpaList = List.of(mock(IdTypeJpaEntity.class));
		List<IdTypeEntity> entities = List.of(mock(IdTypeEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(IdTypeEntity.class)).isEmpty());
	}
}