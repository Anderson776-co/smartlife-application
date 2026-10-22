package co.com.smartlife.smartlifeapi.visits.service.output.port;

import co.com.smartlife.smartlifeapi.visits.service.output.dto.MessageCatalogDTO;

public interface MessageCatalogOutputPort {

	MessageCatalogDTO findByCodeAndLocale(String code, String locale);
}