package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.repository.criteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.FilterOperator;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

@SuppressWarnings({ "unchecked", "rawtypes" })
class CriteriaToJpaConverterTest {

	private Root<Object> root;
	private Path<Object> path;
	private CriteriaQuery<?> query;
	private CriteriaBuilder cb;

	@BeforeEach
	void setUp() {
		root = mock(Root.class);
		path = mock(Path.class);
		query = mock(CriteriaQuery.class);
		cb = mock(CriteriaBuilder.class);
		when(root.get("field")).thenReturn(path);
	}

	private void apply(final Filter filter) {
		final Specification<Object> specification = CriteriaToJpaConverter.toSpecification(Criteria.of(filter));
		specification.toPredicate(root, query, cb);
	}

	@Test
	void equalShouldCompareThePathWithTheValue() {
		apply(Filter.equal("field", "x"));

		verify(cb).equal(path, "x");
	}

	@Test
	void notEqualShouldCompareThePathWithTheValue() {
		apply(new Filter("field", FilterOperator.NOT_EQUAL, "x"));

		verify(cb).notEqual(path, "x");
	}

	@Test
	void inShouldAcceptACollection() {
		final List<String> values = List.of("a", "b");

		apply(new Filter("field", FilterOperator.IN, values));

		verify(path).in(values);
	}

	@Test
	void inShouldAcceptAnArray() {
		apply(new Filter("field", FilterOperator.IN, new Object[] { "a", "b" }));

		verify(path).in(List.of("a", "b"));
	}

	@Test
	void inShouldAcceptASingleValue() {
		final UUID id = UUID.randomUUID();

		apply(new Filter("field", FilterOperator.IN, id));

		verify(path).in(List.of(id));
	}

	@Test
	void containsShouldUseALikeWithWildcards() {
		final Expression<String> text = mock(Expression.class);
		when(path.as(String.class)).thenReturn((Expression) text);

		apply(new Filter("field", FilterOperator.CONTAINS, "abc"));

		verify(cb).like(text, "%abc%");
	}

	@Test
	void greaterThanShouldCompareStrictly() {
		final Instant value = Instant.parse("2026-11-03T10:00:00Z");

		apply(Filter.greaterThan("field", value));

		verify(cb).greaterThan((Expression<Comparable>) (Expression<?>) path, (Comparable) value);
	}

	@Test
	void lessThanShouldCompareStrictly() {
		final Instant value = Instant.parse("2026-11-03T10:00:00Z");

		apply(Filter.lessThan("field", value));

		verify(cb).lessThan((Expression<Comparable>) (Expression<?>) path, (Comparable) value);
	}

	@Test
	void aDottedFieldShouldBeResolvedThroughEachPart() {
		final Path<Object> authorizator = mock(Path.class);
		final Path<Object> id = mock(Path.class);
		final UUID userId = UUID.randomUUID();
		when(root.get("authorizator")).thenReturn(authorizator);
		when(authorizator.get("id")).thenReturn(id);

		apply(Filter.equal("authorizator.id", userId));

		verify(cb).equal(id, userId);
	}

	@Test
	void everyFilterShouldBecomeAPredicateOfTheSameAnd() {
		final Specification<Object> specification = CriteriaToJpaConverter
				.toSpecification(Criteria.of(Filter.equal("field", 1), Filter.equal("field", 2)));

		specification.toPredicate(root, query, cb);

		verify(cb).equal(path, 1);
		verify(cb).equal(path, 2);
	}

	@Test
	void theSortShouldFollowTheOrderOfTheCriteria() {
		assertEquals(Sort.by(Sort.Direction.ASC, "id"), CriteriaToJpaConverter.toSort(Order.asc("id")));
		assertEquals(Sort.by(Sort.Direction.DESC, "id"), CriteriaToJpaConverter.toSort(Order.desc("id")));
		assertTrue(CriteriaToJpaConverter.toSort(Order.none()).isUnsorted());
	}
}
