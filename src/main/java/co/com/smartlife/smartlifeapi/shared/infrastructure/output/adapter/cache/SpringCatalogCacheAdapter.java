package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.cache;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.cache.CatalogCacheOutputPort;

/**
 * Borra de la cache (Redis o la que este configurada) lo que guardan los adaptadores de Strapi con
 * {@code @Cacheable}. La llave debe ser la misma que ellos usan: {@code codigo:idioma}.
 */
@Component
public class SpringCatalogCacheAdapter implements CatalogCacheOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringCatalogCacheAdapter.class);

	private static final Map<CatalogTypeDomain, String> CACHE_NAMES = Map.of(
			CatalogTypeDomain.MESSAGE, "message-catalog",
			CatalogTypeDomain.NOTIFICATION, "notification-catalog");

	private final CacheManager cacheManager;

	public SpringCatalogCacheAdapter(final CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void evict(final CatalogTypeDomain catalog, final String code, final String locale) {
		final Cache cache = cacheOf(catalog);
		if (cache != null) {
			cache.evict(code + ":" + locale);
		}
	}

	@Override
	public void evictAll(final CatalogTypeDomain catalog) {
		final Cache cache = cacheOf(catalog);
		if (cache != null) {
			cache.clear();
		}
	}

	private Cache cacheOf(final CatalogTypeDomain catalog) {
		final String name = CACHE_NAMES.get(catalog);
		final Cache cache = name == null ? null : cacheManager.getCache(name);
		if (cache == null) {
			LOGGER.warn("No existe una caché para el catálogo {}: no se borra nada", catalog);
		}
		return cache;
	}
}
