package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.cache.redis.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.cache.redis.config.LoggingCacheErrorHandler;

class LoggingCacheErrorHandlerTest {

	private final LoggingCacheErrorHandler handler = new LoggingCacheErrorHandler();
	private final Cache cache = new ConcurrentMapCache("message-catalog");
	private final RuntimeException redisDown = new IllegalStateException("Redis caído");

	@Test
	void handleCacheGetErrorShouldNotThrow() {
		assertDoesNotThrow(() -> handler.handleCacheGetError(redisDown, cache, "VISIT_AUTHORIZED:es"));
	}

	@Test
	void handleCachePutErrorShouldNotThrow() {
		assertDoesNotThrow(() -> handler.handleCachePutError(redisDown, cache, "VISIT_AUTHORIZED:es", "valor"));
	}

	@Test
	void handleCacheEvictErrorShouldNotThrow() {
		assertDoesNotThrow(() -> handler.handleCacheEvictError(redisDown, cache, "VISIT_AUTHORIZED:es"));
	}

	@Test
	void handleCacheClearErrorShouldNotThrow() {
		assertDoesNotThrow(() -> handler.handleCacheClearError(redisDown, cache));
	}
}