package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.repository.criteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filters;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;

class CriteriaToJpaConverterPageTest {

	@Test
	void limitAndOffsetShouldBecomeThePageOfSpringData() {
		final Pageable pageable = CriteriaToJpaConverter
				.toPageable(new Criteria(Filters.of(), Order.desc("authorizationStartDate"), 10, 30));

		assertEquals(3, pageable.getPageNumber());
		assertEquals(10, pageable.getPageSize());
		assertEquals(Sort.by(Sort.Direction.DESC, "authorizationStartDate"), pageable.getSort());
	}

	@Test
	void withoutLimitTheResultIsNotPaged() {
		final Pageable pageable = CriteriaToJpaConverter.toPageable(new Criteria(Filters.of(), Order.asc("id"), null, null));

		assertFalse(pageable.isPaged());
		assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageable.getSort());
	}

	@Test
	void withoutOrderTheSortIsEmpty() {
		assertTrue(CriteriaToJpaConverter.toPageable(Criteria.of()).getSort().isUnsorted());
	}
}
