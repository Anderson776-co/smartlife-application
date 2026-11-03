package co.com.smartlife.smartlifeapi.shared.service.output.port.cache;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;

public interface CatalogCacheOutputPort {

	void evict(CatalogTypeDomain catalog, String code, String locale);

	void evictAll(CatalogTypeDomain catalog);
}
