package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filters;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;
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
	@SuppressWarnings("unchecked")
	void matchingShouldSearchWithTheSpecificationAndThePageOfTheCriteriaAndReturnTheMappedList() {
		final Criteria criteria = new Criteria(Filters.of(Filter.equalTo("authorizator.id", UUID.randomUUID())),
				Order.desc("authorizationStartDate"), 5, 10);
		final List<VisitAuthorizationJpaEntity> jpaList = List.of(mock(VisitAuthorizationJpaEntity.class),
				mock(VisitAuthorizationJpaEntity.class));
		final List<VisitAuthorizationEntity> mapped = List.of(mock(VisitAuthorizationEntity.class),
				mock(VisitAuthorizationEntity.class));
		when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(jpaList));
		when(mapper.toEntity(jpaList)).thenReturn(mapped);

		assertSame(mapped, adapter.matching(criteria));

		final ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
		verify(repository).findAll(any(Specification.class), pageable.capture());
		assertEquals(2, pageable.getValue().getPageNumber());
		assertEquals(5, pageable.getValue().getPageSize());
		assertEquals(Sort.by(Sort.Direction.DESC, "authorizationStartDate"), pageable.getValue().getSort());
	}

	@Test
	@SuppressWarnings("unchecked")
	void countShouldCountWithTheSpecificationOfTheCriteria() {
		when(repository.count(any(Specification.class))).thenReturn(7L);

		assertEquals(7L, adapter.count(Criteria.of(Filter.equalTo("authorizator.id", UUID.randomUUID()))));
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
