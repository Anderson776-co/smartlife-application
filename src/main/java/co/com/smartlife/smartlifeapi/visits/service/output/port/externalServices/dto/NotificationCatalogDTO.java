package co.com.smartlife.smartlifeapi.visits.service.output.dto;

import java.io.Serializable;

public record NotificationCatalogDTO(String code, String subject, String content, String locale)
		implements Serializable {
}