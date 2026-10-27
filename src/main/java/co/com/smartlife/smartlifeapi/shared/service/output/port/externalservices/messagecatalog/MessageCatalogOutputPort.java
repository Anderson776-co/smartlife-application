package co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog;

import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.dto.MessageCatalogDTO;

public interface MessageCatalogOutputPort {

	MessageCatalogDTO findByCodeAndLocale(String code, String locale);
}