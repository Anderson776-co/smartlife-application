package co.com.smartlife.smartlifeapi.shared.repository.criteria;

import java.util.List;

public record Filters(List<Filter> values) {
	
	public Filters{
		values = List.copyOf(values);
	}
	
	public static Filters of(Filter... filters) {
		return new Filters(List.of(filters));
	}

}
