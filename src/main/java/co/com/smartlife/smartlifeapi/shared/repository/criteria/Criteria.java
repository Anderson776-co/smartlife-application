package co.com.smartlife.smartlifeapi.shared.repository.criteria;

public record Criteria(Filters filters, Order order, Integer limit, Integer offset) {
    public static Criteria of(Filter... f) { 
    	return new Criteria(Filters.of(f), Order.none(), null, null); 
    }
    
    public Criteria orderedBy(Order o) { 
    	return new Criteria(filters, o, limit, offset); 
    }
}
