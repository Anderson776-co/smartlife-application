package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

/** Se cumple cuando el texto fue ingresado: no es nulo ni esta en blanco (se ignoran los espacios de los extremos). */
public final class IsStringPresentSpecification extends Specification<String> {

	@Override
	public boolean isSatisfiedBy(final String data) {
		return !TextHelper.isEmptyWithTrim(data);
	}

}
