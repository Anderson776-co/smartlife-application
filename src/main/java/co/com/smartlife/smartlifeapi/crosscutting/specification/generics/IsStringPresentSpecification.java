package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public final class IsStringPresentSpecification extends Specification<String> {

	@Override
	public boolean isSatisfiedBy(final String data) {
		return !TextHelper.isEmptyWithTrim(data);
	}

}
