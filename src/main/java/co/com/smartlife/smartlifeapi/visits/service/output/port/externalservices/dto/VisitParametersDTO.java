package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

public record VisitParametersDTO(
		Duration maxAuthorizationDuration,
		Duration maxAnticipation,
		Duration startTolerance,
		long documentMaxSizeInBytes,
		Set<String> documentAllowedTypes,
		String qrBaseUrl,
		ZoneId timeZone) {

	private static final String PATH_SEPARATOR = "/";

	public String qrUrlFor(final UUID authorizationId) {
		final String base = qrBaseUrl.endsWith(PATH_SEPARATOR) ? qrBaseUrl : qrBaseUrl + PATH_SEPARATOR;
		return base + authorizationId;
	}

}
