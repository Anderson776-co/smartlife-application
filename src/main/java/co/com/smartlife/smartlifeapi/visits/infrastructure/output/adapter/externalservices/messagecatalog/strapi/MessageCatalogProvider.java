package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.messagecatalog.strapi;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.MessageCatalogOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.MessageCatalogDTO;

@Component
public class MessageCatalogProvider {
	private static MessageCatalogOutputPort messageCatalogOutputPort;

	public MessageCatalogProvider(final MessageCatalogOutputPort messageCatalogOutputPort) {
		MessageCatalogProvider.messageCatalogOutputPort = messageCatalogOutputPort;
	}
	
	public static MessageCatalogDTO getMessage(final String code, final String locale) {
		return messageCatalogOutputPort.findByCodeAndLocale(code, locale);
	}	
}
