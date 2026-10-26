package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.notificationgateway.resend.dto;

import java.util.List;

public record ResendEmailRequest(String from, List<String> to, String subject, String html) {
}