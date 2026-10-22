package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.MessageCatalogDTO;

public interface MessageCatalogOutputPort {

	MessageCatalogDTO findByCodeAndLocale(String code, String locale);
}