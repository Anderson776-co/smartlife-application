package co.com.smartlife.smartlifeapi.shared.domain.catalog;

/**
 * Cambio ocurrido en un catalogo de Strapi: que paso, en cual catalogo y, si se conoce,
 * a cual entrada (codigo e idioma) afecto.
 */
public record CatalogInvalidationDomain(CatalogEventDomain event, CatalogTypeDomain catalog, String code,
		String locale) {

	public CatalogInvalidationDomain {
		event = event == null ? CatalogEventDomain.OTHER : event;
		catalog = catalog == null ? CatalogTypeDomain.UNKNOWN : catalog;
		code = code == null ? "" : code.trim();
		locale = locale == null ? "" : locale.trim();
	}

	/** Indica si el evento identifica la entrada afectada; si no, hay que vaciar el catalogo completo. */
	public boolean hasEntry() {
		return !code.isBlank() && !locale.isBlank();
	}

	/** Indica si este cambio obliga a borrar algo de la cache. */
	public boolean requiresEviction() {
		return event.invalidatesCache() && catalog != CatalogTypeDomain.UNKNOWN;
	}
}
