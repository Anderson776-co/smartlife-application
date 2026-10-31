package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.FileStorageOutputPort;

/**
 * Guarda en R2 el documento de soporte de una autorizacion. La clave se arma con el id de la autorizacion
 * y una extension propia del tipo validado: el nombre del archivo del cliente nunca llega al almacenamiento.
 */
@Component
public class VisitDocumentStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisitDocumentStorage.class);
	private static final String KEY_FORMAT = "visits/authorizations/%s/document.%s";
	private static final String DEFAULT_EXTENSION = "bin";
	private static final Map<String, String> EXTENSIONS = Map.of(
			"application/pdf", "pdf", "image/jpeg", "jpg", "image/png", "png");

	private final FileStorageOutputPort fileStorage;

	public VisitDocumentStorage(final FileStorageOutputPort fileStorage) {
		this.fileStorage = fileStorage;
	}

	/** @return la referencia (clave en R2) que se guarda en la autorizacion. */
	public String store(final UUID authorizationId, final AttachedDocumentDomain document) {
		final String key = KEY_FORMAT.formatted(authorizationId,
				EXTENSIONS.getOrDefault(document.contentType(), DEFAULT_EXTENSION));
		try {
			return fileStorage.upload(key, document.content(), document.contentType());
		} catch (final RuntimeException exception) {
			throw SmartLifeException.create(exception,
					MessageCatalogProvider.getContent("USER_MESSAGE_VISIT_DOCUMENT_STORAGE_FAILED"),
					MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_VISIT_DOCUMENT_STORAGE_FAILED")
							+ ": " + exception.getMessage());
		}
	}

	/** Compensacion cuando falla el guardado en base de datos: no debe dejar huerfano el archivo ni ocultar el error original. */
	public void discard(final String key) {
		try {
			fileStorage.delete(key);
		} catch (final RuntimeException exception) {
			LOGGER.error("No se pudo eliminar el documento huerfano {}: {}", key, exception.getMessage(), exception);
		}
	}

}
