package co.com.smartlife.smartlifeapi.crosscutting.specification.base;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public final class NotSpecification<T> extends Specification<T> {
	
	Specification<T> condition;
	
	public NotSpecification(final Specification<T> condition) {
		super();
		this.condition = condition;
	}

	@Override
	public boolean isSatisfiedBy(final T data) {
		return !condition.isSatisfiedBy(data);
	}

}
