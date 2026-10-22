package co.com.smartlife.smartlifeapi.visits.service.output.dto;

import java.io.Serializable;

public record MessageCatalogDTO(String code, String title, String content, String locale)
		implements Serializable {
}