package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public class IsMandatorySpecification extends Specification<Object>{

	@Override
	public boolean isSatisfiedBy(Object data) {
		return !ObjectHelper.isNull(data);
	}

}
