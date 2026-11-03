package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

/** Se cumple cuando el largo del texto (sin los espacios de los extremos) esta entre el minimo y el maximo. */
public final class StringLengthIsValidSpecification extends Specification<String> {

	private final int min;
	private final int max;

	public StringLengthIsValidSpecification(final int min, final int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean isSatisfiedBy(final String data) {
		return TextHelper.lengthIsValid(data, min, max, true);
	}

}
