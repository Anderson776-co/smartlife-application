package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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

class VisitAuthorizationJpaRepositoryAdapterConsultTest {

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
	void findByAuthorizatorIdShouldReturnTheMappedListKeepingTheRepositoryOrder() {
		UUID authorizatorId = UUID.randomUUID();
		List<VisitAuthorizationJpaEntity> jpaList = List.of(mock(VisitAuthorizationJpaEntity.class),
				mock(VisitAuthorizationJpaEntity.class));
		List<VisitAuthorizationEntity> mapped = List.of(mock(VisitAuthorizationEntity.class),
				mock(VisitAuthorizationEntity.class));
		when(repository.findByAuthorizatorIdOrderByAuthorizationStartDateDesc(authorizatorId)).thenReturn(jpaList);
		when(mapper.toEntity(jpaList)).thenReturn(mapped);

		assertSame(mapped, adapter.findByAuthorizatorId(authorizatorId));
	}

	@Test
	void findByAuthorizatorIdShouldReturnAnEmptyListWhenTheAuthorizatorHasNone() {
		UUID authorizatorId = UUID.randomUUID();
		when(repository.findByAuthorizatorIdOrderByAuthorizationStartDateDesc(authorizatorId)).thenReturn(List.of());

		assertEquals(0, adapter.findByAuthorizatorId(authorizatorId).size());
	}

	@Test
	void findByIdAndAuthorizatorIdShouldReturnTheMappedEntityWhenItBelongsToTheAuthorizator() {
		UUID id = UUID.randomUUID();
		UUID authorizatorId = UUID.randomUUID();
		VisitAuthorizationJpaEntity jpaEntity = mock(VisitAuthorizationJpaEntity.class);
		VisitAuthorizationEntity entity = mock(VisitAuthorizationEntity.class);
		when(repository.findByIdAndAuthorizatorId(id, authorizatorId)).thenReturn(Optional.of(jpaEntity));
		when(mapper.toEntity(jpaEntity)).thenReturn(entity);

		Optional<VisitAuthorizationEntity> result = adapter.findByIdAndAuthorizatorId(id, authorizatorId);

		assertTrue(result.isPresent());
		assertSame(entity, result.get());
	}

	@Test
	void findByIdAndAuthorizatorIdShouldReturnEmptyWhenItBelongsToAnotherAuthorizator() {
		UUID id = UUID.randomUUID();
		UUID anotherAuthorizatorId = UUID.randomUUID();
		when(repository.findByIdAndAuthorizatorId(id, anotherAuthorizatorId)).thenReturn(Optional.empty());

		assertTrue(adapter.findByIdAndAuthorizatorId(id, anotherAuthorizatorId).isEmpty());
	}
}
