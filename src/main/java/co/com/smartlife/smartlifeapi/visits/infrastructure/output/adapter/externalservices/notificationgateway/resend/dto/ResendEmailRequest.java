package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ResendEmailRequest(String from, List<String> to, String subject, String html,
		@JsonInclude(JsonInclude.Include.NON_EMPTY) List<ResendAttachment> attachments) {
	
	public ResendEmailRequest(final String from, final List<String> to, final String subject, final String html) {
		this(from, to, subject, html, List.of());
	}
}