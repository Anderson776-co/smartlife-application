package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto;

import java.io.Serializable;

public record NotificationCatalogDTO(String code, String subject, String content, String locale)
		implements Serializable {
}