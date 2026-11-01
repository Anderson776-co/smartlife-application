package co.com.smartlife.smartlifeapi.shared.domain.catalog;

/** Catalogo de Strapi cuyo contenido se guarda en cache. */
public enum CatalogTypeDomain {
	MESSAGE,
	NOTIFICATION,
	/** Modelo que no pertenece a ningun catalogo conocido: se ignora. */
	UNKNOWN
}
