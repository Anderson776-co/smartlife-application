package co.com.smartlife.smartlifeapi.crosscutting.specification.base;

import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public final class OrSpecification<T> extends Specification<T> {
	
	Specification<T> left;
	Specification<T> right;
	
	public OrSpecification(final Specification<T> left, final Specification<T> right) {
		super();
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean isSatisfiedBy(final T data) {
		return left.isSatisfiedBy(data) || right.isSatisfiedBy(data);
	}

}
