package co.com.smartlife.smartlifeapi.crosscutting.specification.generics;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.specification.Specification;

public final class IsUUIDPresentSpecification extends Specification<UUID> {

	@Override
	public boolean isSatisfiedBy(final UUID data) {
		return !UUIDHelper.getUUIDHelper().isDefaultUUID(data);
	}

}