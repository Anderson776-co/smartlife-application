package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public final class StringFormatIsValidSpecification extends Specification<String> {

	private final String pattern;

	public StringFormatIsValidSpecification(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean isSatisfiedBy(final String data) {
		return TextHelper.formatIsValid(data, pattern, true);
	}

}
