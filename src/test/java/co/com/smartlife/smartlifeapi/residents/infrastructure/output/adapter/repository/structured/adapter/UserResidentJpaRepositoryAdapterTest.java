package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.UserResidentJpaRepository;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.UserResidentJpaEntity;
import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.mapper.UserResidentMapper;
import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;

class UserResidentJpaRepositoryAdapterTest {

	private UserResidentJpaRepository repository;
	private UserResidentMapper mapper;
	private UserResidentJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UserResidentJpaRepository.class);
		mapper = mock(UserResidentMapper.class);
		adapter = new UserResidentJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UserResidentJpaEntity jpaEntity = mock(UserResidentJpaEntity.class);
		UserResidentEntity entity = mock(UserResidentEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UserResidentEntity> result = adapter.findById(id);

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
		List<UserResidentJpaEntity> jpaList = List.of(mock(UserResidentJpaEntity.class));
		List<UserResidentEntity> entities = List.of(mock(UserResidentEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UserResidentEntity.class)).isEmpty());
	}
}