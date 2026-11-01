package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.adapter;

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

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.ApartmentJpaRepository;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.ApartmentJpaEntity;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.mapper.ApartmentMapper;
import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.ApartmentEntity;

class ApartmentJpaRepositoryAdapterTest {

	private ApartmentJpaRepository repository;
	private ApartmentMapper mapper;
	private ApartmentJpaRepositoryAdapter adapter;

	@BeforeEach
	void setUp() {
		repository = mock(ApartmentJpaRepository.class);
		mapper = mock(ApartmentMapper.class);
		adapter = new ApartmentJpaRepositoryAdapter(repository, mapper);
	}

	@Test
	void createShouldMapTheEntityAndSaveIt() {
		ApartmentEntity entity = mock(ApartmentEntity.class);
		ApartmentJpaEntity jpaEntity = mock(ApartmentJpaEntity.class);
		when(mapper.toJpaEntity(entity)).thenReturn(jpaEntity);

		adapter.create(entity);

		verify(repository).save(jpaEntity);
	}

	@Test
	void findByIdShouldReturnTheMappedEntityWhenItExists() {
		UUID id = UUID.randomUUID();
		ApartmentJpaEntity jpaEntity = mock(ApartmentJpaEntity.class);
		ApartmentEntity entity = mock(ApartmentEntity.class);
		when(repository.findById(id)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<ApartmentEntity> result = adapter.findById(id);

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
		List<ApartmentJpaEntity> jpaList = List.of(mock(ApartmentJpaEntity.class));
		List<ApartmentEntity> entities = List.of(mock(ApartmentEntity.class));
		when(repository.findAll()).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(entities);

		assertSame(entities, adapter.findAll());
	}

	@Test
	void findByFilterShouldReturnAnEmptyListForNow() {
		assertTrue(adapter.findByFilter(mock(ApartmentEntity.class)).isEmpty());
	}

	@Test
	void updateShouldNotFailForNow() {
		assertDoesNotThrow(() -> adapter.update(UUID.randomUUID(), mock(ApartmentEntity.class)));
	}
}