package co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto;

import java.io.Serializable;

public record MessageCatalogDTO(String code, String title, String content, String locale)
		implements Serializable {
}