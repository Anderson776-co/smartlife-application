package co.com.smartlife.smartlifeapi.shared.service.output.port.cache;

import co.com.smartlife.smartlifeapi.shared.domain.catalog.CatalogTypeDomain;

/** Cache de los catalogos de Strapi. */
public interface CatalogCacheOutputPort {

	/** Borra la entrada de un codigo en un idioma. */
	void evict(CatalogTypeDomain catalog, String code, String locale);

	/** Borra todas las entradas del catalogo. */
	void evictAll(CatalogTypeDomain catalog);
}
