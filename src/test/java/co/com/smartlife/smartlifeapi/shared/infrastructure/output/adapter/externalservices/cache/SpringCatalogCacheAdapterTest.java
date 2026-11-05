package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.externalservices.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;

class SpringCatalogCacheAdapterTest {

	private CacheManager cacheManager;
	private SpringCatalogCacheAdapter adapter;

	@BeforeEach
	void setUp() {
		cacheManager = new ConcurrentMapCacheManager("message-catalog", "notification-catalog");
		adapter = new SpringCatalogCacheAdapter(cacheManager);
		cacheManager.getCache("message-catalog").put("CODE:es", "mensaje");
		cacheManager.getCache("message-catalog").put("CODE:en", "message");
		cacheManager.getCache("message-catalog").put("OTHER:es", "otro");
		cacheManager.getCache("notification-catalog").put("MAIL:es", "plantilla");
	}

	@Test
	void shouldEvictOnlyTheKeyWithTheSameCodeAndLocale() {
		adapter.evict(CatalogTypeDomain.MESSAGE, "CODE", "es");

		assertNull(cacheManager.getCache("message-catalog").get("CODE:es"));
		assertNotNull(cacheManager.getCache("message-catalog").get("CODE:en"));
		assertNotNull(cacheManager.getCache("message-catalog").get("OTHER:es"));
		assertNotNull(cacheManager.getCache("notification-catalog").get("MAIL:es"));
	}

	@Test
	void shouldUseTheNotificationCacheForNotifications() {
		adapter.evict(CatalogTypeDomain.NOTIFICATION, "MAIL", "es");

		assertNull(cacheManager.getCache("notification-catalog").get("MAIL:es"));
		assertNotNull(cacheManager.getCache("message-catalog").get("CODE:es"));
	}

	@Test
	void shouldClearTheWholeCatalog() {
		adapter.evictAll(CatalogTypeDomain.MESSAGE);

		assertNull(cacheManager.getCache("message-catalog").get("CODE:es"));
		assertNull(cacheManager.getCache("message-catalog").get("OTHER:es"));
		assertNotNull(cacheManager.getCache("notification-catalog").get("MAIL:es"));
	}

	@Test
	void shouldDoNothingForAnUnknownCatalog() {
		adapter.evict(CatalogTypeDomain.UNKNOWN, "CODE", "es");
		adapter.evictAll(CatalogTypeDomain.UNKNOWN);

		assertNotNull(cacheManager.getCache("message-catalog").get("CODE:es"));
		assertNotNull(cacheManager.getCache("notification-catalog").get("MAIL:es"));
	}
}
