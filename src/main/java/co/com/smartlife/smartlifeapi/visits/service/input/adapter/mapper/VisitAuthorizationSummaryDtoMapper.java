package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

public final class VisitAuthorizationSummaryDtoMapper {

	private VisitAuthorizationSummaryDtoMapper() {
	}

	public static VisitAuthorizationSummaryDtoResponse toDto(final VisitAuthorizationDomain authorization) {
		final VisitorDomain visitor = authorization.visitor();
		return new VisitAuthorizationSummaryDtoResponse(authorization.id(), authorization.authorizationStartDate(),
				authorization.authorizationEndDate(), authorization.registerDate(), authorization.observation(),
				authorization.qrCode(), authorization.propertyLocation(), authorization.visitReasonName(),
				!TextHelper.isEmptyWithTrim(authorization.fileReference()), visitor.documentNumber(),
				fullName(visitor), visitor.email(), visitor.vehiclePlate(), authorization.status());
	}

	public static VisitAuthorizationPageDtoResponse toPage(final List<VisitAuthorizationDomain> all, final int page,
			final int size) {
		final int safePage = Math.max(page, 0);
		final int safeSize = size < 1 ? ListVisitAuthorizationsDtoRequest.DEFAULT_PAGE_SIZE
				: Math.min(size, ListVisitAuthorizationsDtoRequest.MAX_PAGE_SIZE);
		final long total = all.size();
		final int totalPages = (int) ((total + safeSize - 1) / safeSize);
		final long from = (long) safePage * safeSize;
		final List<VisitAuthorizationDomain> slice = from >= total ? List.of()
				: all.subList((int) from, (int) Math.min(from + safeSize, total));
		return new VisitAuthorizationPageDtoResponse(slice.stream().map(VisitAuthorizationSummaryDtoMapper::toDto).toList(),
				safePage, safeSize, total, totalPages);
	}

	private static String fullName(final VisitorDomain visitor) {
		return Stream.of(visitor.firstName(), visitor.middleName(), visitor.lastName(), visitor.secondLastName())
				.filter(part -> !TextHelper.isEmptyWithTrim(part)).collect(Collectors.joining(" "));
	}
}
