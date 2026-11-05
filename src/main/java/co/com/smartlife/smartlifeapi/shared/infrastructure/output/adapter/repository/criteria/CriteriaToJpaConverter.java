package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.repository.criteria;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import co.com.smartlife.smartlifeapi.shared.repository.criteria.Criteria;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Filter;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.Order;
import co.com.smartlife.smartlifeapi.shared.repository.criteria.OrderType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public final class CriteriaToJpaConverter {

	private CriteriaToJpaConverter() {
	}

	public static <T> Specification<T> toSpecification(final Criteria c) {
		return (root, query, cb) -> cb.and(c.filters().values().stream()
				.map(f -> toPredicate(f, root, cb)).toArray(Predicate[]::new));
	}

	public static Sort toSort(final Order o) {
		return o.hasOrder() ? Sort.by(o.type() == OrderType.DESC ? Sort.Direction.DESC : Sort.Direction.ASC, o.field())
							: Sort.unsorted();
	}

	public static Pageable toPageable(final Criteria c) {
		final Sort sort = toSort(c.order());
		if (c.limit() == null || c.limit() < 1) {
			return Pageable.unpaged(sort);
		}
		final int offset = c.offset() == null ? 0 : c.offset();
		return PageRequest.of(offset / c.limit(), c.limit(), sort);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Predicate toPredicate(final Filter filter, final Root<?> root, final CriteriaBuilder cb) {
		final Path<?> path = pathOf(root, filter.field());
		switch (filter.operator()) {
			case EQUAL:
				return cb.equal(path, filter.value());
			case NOT_EQUAL:
				return cb.notEqual(path, filter.value());
			case IN:
				return path.in(inValues(filter.value()));
			case CONTAINS:
				return cb.like(path.as(String.class), "%" + filter.value() + "%");
			case GREATER_THAN:
				return cb.greaterThan((Expression<Comparable>) (Expression<?>) path, (Comparable) filter.value());
			case LESS_THAN:
				return cb.lessThan((Expression<Comparable>) (Expression<?>) path, (Comparable) filter.value());
			default:
				throw new IllegalArgumentException("Unsupported filter operator: " + filter.operator());
		}
	}

	/** Admite rutas anidadas separadas por punto, por ejemplo "authorizator.id". */
	private static Path<?> pathOf(final Root<?> root, final String field) {
		Path<?> path = root;
		for (final String part : field.split("\\.")) {
			path = path.get(part);
		}
		return path;
	}

	private static Collection<?> inValues(final Object value) {
		if (value instanceof Collection<?> values) {
			return values;
		}
		if (value instanceof Object[] values) {
			return List.of(values);
		}
		return List.of(value);
	}

}
