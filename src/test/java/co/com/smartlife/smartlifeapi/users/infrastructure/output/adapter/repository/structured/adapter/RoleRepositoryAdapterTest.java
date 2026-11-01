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

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.RoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.RoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.RoleMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.RoleEntity;

class RoleRepositoryAdapterTest {

	private RoleJpaRepository repository;
	private RoleMapper mapper;
	private RoleRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(RoleJpaRepository.class);
		mapper = mock(RoleMapper.class);
		adapter = new RoleRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		RoleJpaEntity jpaEntity = mock(RoleJpaEntity.class);
		RoleEntity entity = mock(RoleEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<RoleEntity> result = adapter.findById(id);

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
		List<RoleJpaEntity> jpaList = List.of(mock(RoleJpaEntity.class));
		List<RoleEntity> entities = List.of(mock(RoleEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(RoleEntity.class)).isEmpty());
	}
}