package co.com.smartlife.smartlifeapi.shared.repository.criteria;

public record Filter(String field, FilterOperator operator, Object value) {

	public static Filter equal(String field, Object value) {
		return new Filter(field, FilterOperator.EQUAL, value);
	}

	public static Filter greaterThan(String field, Object value) {
		return new Filter(field, FilterOperator.GREATER_THAN, value);
	}

	public static Filter lessThan(String field, Object value) {
		return new Filter(field, FilterOperator.LESS_THAN, value);
	}

}
