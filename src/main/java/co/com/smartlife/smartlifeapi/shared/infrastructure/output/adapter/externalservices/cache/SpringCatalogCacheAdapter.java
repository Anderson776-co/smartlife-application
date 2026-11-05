package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.cache;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.cache.CatalogCacheOutputPort;

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
		cacheOf(catalog).ifPresent(cache -> cache.evict(code + ":" + locale));
	}

	@Override
	public void evictAll(final CatalogTypeDomain catalog) {
		cacheOf(catalog).ifPresent(Cache::clear);
	}

	private Optional<Cache> cacheOf(final CatalogTypeDomain catalog) {
		final Optional<Cache> cache = Optional.ofNullable(CACHE_NAMES.get(catalog)).map(cacheManager::getCache);
		if (cache.isEmpty()) {
			LOGGER.warn("No existe una caché para el catálogo {}: no se borra nada", catalog);
		}
		return cache;
	}
}
