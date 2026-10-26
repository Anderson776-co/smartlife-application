package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.strapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.LocalHelper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogOutputPort;

@Component
public class MessageCatalogProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageCatalogProvider.class);
    private static MessageCatalogOutputPort messageCatalogOutputPort;

    public MessageCatalogProvider(final MessageCatalogOutputPort messageCatalog) {
        init(messageCatalog);
    }

    /** Lo usa Spring y también tus pruebas unitarias (con un catálogo falso). */
    public static void init(final MessageCatalogOutputPort port) {
        MessageCatalogProvider.messageCatalogOutputPort = port;
    }

    public static String getContent(final String code) {
        return getContent(code, LocalHelper.getCurrentLocal());
    }

    public static String getContent(final String code, final String locale) {
        try {
            return messageCatalogOutputPort.findByCodeAndLocale(code, locale).content();
        } catch (final RuntimeException exception) {
            LOGGER.warn("No se pudo resolver el mensaje {} ({}): {}", code, locale, exception.getMessage());
            return code;   // nunca deja una excepción sin mensaje
        }
    }

}
