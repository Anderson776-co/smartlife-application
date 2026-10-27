package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

class UserRepositoryAdapterTest {

	private UserJpaRepository repository;
	private UserMapper mapper;
	private UserRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UserJpaRepository.class);
		mapper = mock(UserMapper.class);
		adapter = new UserRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		UserEntity entity = mock(UserEntity.class);
		UserJpaEntity jpaEntity = mock(UserJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UserJpaEntity jpaEntity = mock(UserJpaEntity.class);
		UserEntity entity = mock(UserEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UserEntity> result = adapter.findById(id);

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
		List<UserJpaEntity> jpaList = List.of(mock(UserJpaEntity.class));
		List<UserEntity> entities = List.of(mock(UserEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UserEntity.class)).isEmpty());
	}

	@Test
	void findByEmailShouldReturnTheMappedEntityWhenItExists() {
		UserJpaEntity jpaEntity = mock(UserJpaEntity.class);
		UserEntity entity = mock(UserEntity.class);
		when(repository.findByEmail("ana@test.com")).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UserEntity> result = adapter.findByEmail("ana@test.com");

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
		UserJpaEntity jpaEntity = mock(UserJpaEntity.class);
		UserEntity entity = mock(UserEntity.class);
		when(repository.findByDocumentNumber("123456789")).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UserEntity> result = adapter.findByDocumentNumber("123456789");

		assertTrue(result.isPresent());
		assertSame(entity, result.get());
	}

	@Test
	void findByDocumentNumberShouldReturnEmptyWhenItDoesNotExist() {
		when(repository.findByDocumentNumber("000")).thenReturn(Optional.empty());

		assertTrue(adapter.findByDocumentNumber("000").isEmpty());
	}

	@Test
	void updateShouldCopyTheFieldsOfTheEntityIntoTheStoredOne() {
		UUID id = UUID.randomUUID();
		UserJpaEntity stored = new UserJpaEntity(id, "Viejo", "Viejo", "Viejo", "Viejo", "000", "viejo@test.com",
				false, false, false, "ext-viejo", null, null);
		UserEntity changes = new UserEntity(id, "Ana", "María", "Pérez", "Gómez", "123456789", "ana@test.com",
				true, true, true, "ext-123", null, null);
		when(repository.findById(id)).thenReturn(Optional.of(stored));

		adapter.update(id, changes);

		assertEquals("Ana", stored.getFirstName());
		assertEquals("María", stored.getMiddleName());
		assertEquals("Pérez", stored.getLastName());
		assertEquals("Gómez", stored.getSecondLastName());
		assertEquals("123456789", stored.getDocumentNumber());
		assertEquals("ana@test.com", stored.getEmail());
		assertEquals(Boolean.TRUE, stored.getEmailConfirmed());
		assertEquals(Boolean.TRUE, stored.getEmailConfirmedDefaultValue());
		assertEquals(Boolean.TRUE, stored.getIsActive());
		verify(repository).save(stored);
	}

	@Test
	void updateShouldFailWhenTheUserDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> adapter.update(id, mock(UserEntity.class)));
	}
}