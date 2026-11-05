package co.com.smartlife.smartlifeapi.shared.repository.criteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class CriteriaModelTest {

	@Test
	void theFilterFactoriesShouldSetTheOperator() {
		assertEquals(new Filter("f", FilterOperator.EQUAL, 1), Filter.equal("f", 1));
		assertEquals(new Filter("f", FilterOperator.GREATER_THAN, 1), Filter.greaterThan("f", 1));
		assertEquals(new Filter("f", FilterOperator.LESS_THAN, 1), Filter.lessThan("f", 1));
	}

	@Test
	void theOrderFactoriesShouldSetTheType() {
		assertEquals(OrderType.ASC, Order.asc("f").type());
		assertEquals(OrderType.DESC, Order.desc("f").type());
		assertEquals(OrderType.NONE, Order.none().type());
		assertTrue(Order.asc("f").hasOrder());
		assertTrue(Order.desc("f").hasOrder());
		assertFalse(Order.none().hasOrder());
	}

	@Test
	void filtersShouldKeepAnImmutableCopyOfTheirValues() {
		final List<Filter> source = new ArrayList<>(List.of(Filter.equal("f", 1)));
		final Filters filters = new Filters(source);
		source.add(Filter.equal("g", 2));

		assertEquals(1, filters.values().size());
		assertThrows(UnsupportedOperationException.class, () -> filters.values().add(Filter.equal("h", 3)));
	}

	@Test
	void aCriteriaOfFiltersStartsWithoutOrderLimitNorOffset() {
		final Criteria criteria = Criteria.of(Filter.equal("f", 1), Filter.equal("g", 2));

		assertEquals(2, criteria.filters().values().size());
		assertEquals(Order.none(), criteria.order());
		assertNull(criteria.limit());
		assertNull(criteria.offset());
	}

	@Test
	void orderedByShouldKeepTheFiltersTheLimitAndTheOffset() {
		final Criteria criteria = new Criteria(Filters.of(Filter.equal("f", 1)), Order.none(), 5, 10)
				.orderedBy(Order.desc("f"));

		assertEquals(Order.desc("f"), criteria.order());
		assertEquals(1, criteria.filters().values().size());
		assertEquals(5, criteria.limit());
		assertEquals(10, criteria.offset());
	}
}
