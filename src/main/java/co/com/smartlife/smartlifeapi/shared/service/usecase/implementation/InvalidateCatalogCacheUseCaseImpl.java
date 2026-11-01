package co.com.smartlife.smartlifeapi.shared.service.usecase.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogInvalidationDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.cache.CatalogCacheOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.usecase.contract.InvalidateCatalogCacheUseCase;

/**
 * Limpia la cache de un catalogo cuando Strapi publica, despublica o borra una entrada.
 * Si el evento no identifica la entrada, vacia el catalogo completo (es lo seguro).
 */
@Service
public class InvalidateCatalogCacheUseCaseImpl implements InvalidateCatalogCacheUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvalidateCatalogCacheUseCaseImpl.class);

	private final CatalogCacheOutputPort catalogCache;

	public InvalidateCatalogCacheUseCaseImpl(final CatalogCacheOutputPort catalogCache) {
		this.catalogCache = catalogCache;
	}

	@Override
	public void execute(final CatalogInvalidationDomain input) {
		if (!input.requiresEviction()) {
			LOGGER.debug("Webhook de Strapi ignorado: evento {} sobre el catálogo {}", input.event(), input.catalog());
			return;
		}

		if (input.hasEntry()) {
			catalogCache.evict(input.catalog(), input.code(), input.locale());
			LOGGER.info("Caché invalidada: catálogo {}, código {} ({})", input.catalog(), input.code(), input.locale());
		} else {
			catalogCache.evictAll(input.catalog());
			LOGGER.info("Caché invalidada: catálogo {} completo", input.catalog());
		}
	}
}
