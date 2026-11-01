package co.com.smartlife.smartlifeapi.shared.service.usecase.implementation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogEventDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogInvalidationDomain;
import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.cache.CatalogCacheOutputPort;

class InvalidateCatalogCacheUseCaseImplTest {

	private CatalogCacheOutputPort catalogCache;
	private InvalidateCatalogCacheUseCaseImpl useCase;

	@BeforeEach
	void setUp() {
		catalogCache = mock(CatalogCacheOutputPort.class);
		useCase = new InvalidateCatalogCacheUseCaseImpl(catalogCache);
	}

	@Test
	void shouldEvictTheEntryOnPublish() {
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.PUBLISH, CatalogTypeDomain.MESSAGE, "CODE", "es"));

		verify(catalogCache).evict(CatalogTypeDomain.MESSAGE, "CODE", "es");
	}

	@Test
	void shouldEvictTheEntryOnUnpublishAndDelete() {
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.UNPUBLISH, CatalogTypeDomain.NOTIFICATION, "CODE", "es"));
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.DELETE, CatalogTypeDomain.NOTIFICATION, "OTHER", "en"));

		verify(catalogCache).evict(CatalogTypeDomain.NOTIFICATION, "CODE", "es");
		verify(catalogCache).evict(CatalogTypeDomain.NOTIFICATION, "OTHER", "en");
	}

	@Test
	void shouldClearTheWholeCatalogWhenTheEventDoesNotIdentifyTheEntry() {
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.PUBLISH, CatalogTypeDomain.MESSAGE, "", ""));

		verify(catalogCache).evictAll(CatalogTypeDomain.MESSAGE);
	}

	@Test
	void shouldIgnoreEventsThatDoNotChangeWhatIsPublished() {
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.OTHER, CatalogTypeDomain.MESSAGE, "CODE", "es"));

		verifyNoInteractions(catalogCache);
	}

	@Test
	void shouldIgnoreUnknownCatalogs() {
		useCase.execute(new CatalogInvalidationDomain(CatalogEventDomain.PUBLISH, CatalogTypeDomain.UNKNOWN, "CODE", "es"));

		verifyNoInteractions(catalogCache);
	}
}
