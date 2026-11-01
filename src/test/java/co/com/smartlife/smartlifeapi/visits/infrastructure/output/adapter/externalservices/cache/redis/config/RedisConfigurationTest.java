package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.cache.redis.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class RedisConfigurationTest {

	@Test
	void theErrorHandlerShouldBeTheLoggingOne() {
		assertInstanceOf(LoggingCacheErrorHandler.class, new RedisConfiguration().errorHandler());
	}
}