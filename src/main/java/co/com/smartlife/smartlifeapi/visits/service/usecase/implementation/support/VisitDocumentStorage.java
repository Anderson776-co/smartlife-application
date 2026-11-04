package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.FileStorageOutputPort;

@Component
public class VisitDocumentStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisitDocumentStorage.class);

	private final FileStorageOutputPort fileStorage;

	public VisitDocumentStorage(final FileStorageOutputPort fileStorage) {
		this.fileStorage = fileStorage;
	}

	public String store(final UUID authorizationId, final AttachedDocumentDomain document) {
		final String key = ParameterCatalog.DOCUMENT_STORAGE_KEY_FORMAT.formatted(authorizationId,
				ParameterCatalog.DOCUMENT_EXTENSIONS.getOrDefault(document.contentType(),
						ParameterCatalog.DOCUMENT_DEFAULT_EXTENSION));
		try {
			return fileStorage.upload(key, document.content(), document.contentType());
		} catch (final RuntimeException exception) {
			throw SmartLifeException.create(exception,
					MessageCatalogProvider.getContent("USER_MESSAGE_VISIT_DOCUMENT_STORAGE_FAILED"),
					MessageCatalogProvider.getContent("TECHNICAL_MESSAGE_VISIT_DOCUMENT_STORAGE_FAILED")
							+ ": " + exception.getMessage());
		}
	}

	public void discard(final String key) {
		try {
			fileStorage.delete(key);
		} catch (final RuntimeException exception) {
			LOGGER.error("No se pudo eliminar el documento huerfano {}: {}", key, exception.getMessage(), exception);
		}
	}

}
