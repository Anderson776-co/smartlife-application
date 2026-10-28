package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

public record VisitorResolution(VisitorEntity entity, boolean isNew) {
	public static VisitorResolution existing(final VisitorEntity entity) { return new VisitorResolution(entity, false); }
	public static VisitorResolution created(final VisitorEntity entity) { return new VisitorResolution(entity, true); }
}
