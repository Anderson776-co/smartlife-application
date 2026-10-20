package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.cache.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class LoggingCacheErrorHandler implements CacheErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingCacheErrorHandler.class);

	@Override
	public void handleCacheGetError(final RuntimeException exception, final Cache cache, final Object key) {
		LOGGER.warn("No se pudo leer de la caché {} con la llave {}: {}", cache.getName(), key,
				exception.getMessage());
	}

	@Override
	public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key,
			final Object value) {
		LOGGER.warn("No se pudo guardar en la caché {} con la llave {}: {}", cache.getName(), key,
				exception.getMessage());
	}

	@Override
	public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
		LOGGER.warn("No se pudo borrar de la caché {} la llave {}: {}", cache.getName(), key,
				exception.getMessage());
	}

	@Override
	public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
		LOGGER.warn("No se pudo vaciar la caché {}: {}", cache.getName(), exception.getMessage());
	}
}