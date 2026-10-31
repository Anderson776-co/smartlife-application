package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

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

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UserRoleJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserRoleJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserRoleMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

class UserRoleRepositoryAdapterTest {

	private UserRoleJpaRepository repository;
	private UserRoleMapper mapper;
	private UserRoleRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UserRoleJpaRepository.class);
		mapper = mock(UserRoleMapper.class);
		adapter = new UserRoleRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		UserRoleEntity entity = mock(UserRoleEntity.class);
		UserRoleJpaEntity jpaEntity = mock(UserRoleJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UserRoleJpaEntity jpaEntity = mock(UserRoleJpaEntity.class);
		UserRoleEntity entity = mock(UserRoleEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UserRoleEntity> result = adapter.findById(id);

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
		List<UserRoleJpaEntity> jpaList = List.of(mock(UserRoleJpaEntity.class));
		List<UserRoleEntity> entities = List.of(mock(UserRoleEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UserRoleEntity.class)).isEmpty());
	}

	@Test
	void deleteShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.delete(UUID.randomUUID(), mock(UserRoleEntity.class)));
	}

	@Test
	void findByUserIdShouldReturnTheMappedList() {
		UUID userId = UUID.randomUUID();
		List<UserRoleJpaEntity> jpaList = List.of(mock(UserRoleJpaEntity.class));
		List<UserRoleEntity> entities = List.of(mock(UserRoleEntity.class));
		when(repository.findByUserId(userId)).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findByUserId(userId));
	}

	@Test
	void findByRoleIdShouldReturnTheMappedList() {
		UUID roleId = UUID.randomUUID();
		List<UserRoleJpaEntity> jpaList = List.of(mock(UserRoleJpaEntity.class));
		List<UserRoleEntity> entities = List.of(mock(UserRoleEntity.class));
		when(repository.findByRoleId(roleId)).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findByRoleId(roleId));
	}
}