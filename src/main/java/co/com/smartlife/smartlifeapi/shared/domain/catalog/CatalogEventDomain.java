package co.com.smartlife.smartlifeapi.shared.domain.catalog;

/** Evento del catalogo que llega por webhook. */
public enum CatalogEventDomain {
	PUBLISH,
	UNPUBLISH,
	DELETE,
	/** Cualquier otro evento (prueba, borrador, creacion...): no cambia lo que sirve la API publicada. */
	OTHER;

	/** Solo estos eventos cambian el contenido publicado, y por tanto invalidan la cache. */
	public boolean invalidatesCache() {
		return this != OTHER;
	}
}
