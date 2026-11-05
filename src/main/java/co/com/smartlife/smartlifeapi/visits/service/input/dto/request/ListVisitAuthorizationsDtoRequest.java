package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;

public record ListVisitAuthorizationsDtoRequest(Boolean isActive, Instant from, Instant to, UUID propertyId,
		CurrentUser currentUser, int page, int size) {

	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int MAX_PAGE_SIZE = 50;

	private static final CurrentUser ANONYMOUS = new CurrentUser(UUIDHelper.getUUIDHelper().getDefault(), "",
			Set.of(), UUIDHelper.getUUIDHelper().getDefault());

	public ListVisitAuthorizationsDtoRequest(final Boolean isActive, final Instant from, final Instant to,
			final UUID propertyId, final CurrentUser currentUser) {
		this(isActive, from, to, propertyId, currentUser, 0, DEFAULT_PAGE_SIZE);
	}

	public ListVisitAuthorizationsDtoRequest {
		currentUser = ObjectHelper.getDefault(currentUser, ANONYMOUS);
		page = Math.max(page, 0);
		size = size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
	}
}
