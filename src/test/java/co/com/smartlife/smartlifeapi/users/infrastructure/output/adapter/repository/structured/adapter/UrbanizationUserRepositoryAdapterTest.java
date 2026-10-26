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

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.UrbanizationUserJpaRepository;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationUserJpaEntity;
import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.mapper.UrbanizationUserMapper;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UrbanizationUserEntity;

class UrbanizationUserRepositoryAdapterTest {

	private UrbanizationUserJpaRepository repository;
	private UrbanizationUserMapper mapper;
	private UrbanizationUserRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(UrbanizationUserJpaRepository.class);
		mapper = mock(UrbanizationUserMapper.class);
		adapter = new UrbanizationUserRepositoryAdapter(repository, mapper);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		UrbanizationUserJpaEntity jpaEntity = mock(UrbanizationUserJpaEntity.class);
		UrbanizationUserEntity entity = mock(UrbanizationUserEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<UrbanizationUserEntity> result = adapter.findById(id);

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
		List<UrbanizationUserJpaEntity> jpaList = List.of(mock(UrbanizationUserJpaEntity.class));
		List<UrbanizationUserEntity> entities = List.of(mock(UrbanizationUserEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(UrbanizationUserEntity.class)).isEmpty());
	}
}