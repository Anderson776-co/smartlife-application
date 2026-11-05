package co.com.smartlife.smartlifeapi.shared.repository.criteria;

public record Order(String field, OrderType type) {
	public static Order none()          { return new Order("", OrderType.NONE); }
	public static Order asc(String f)   { return new Order(f, OrderType.ASC); }
	public static Order desc(String f)  { return new Order(f, OrderType.DESC); }
	public boolean hasOrder()           { return type != OrderType.NONE; }
}
