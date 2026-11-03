package co.com.smartlife.smartlifeapi.shared.domain.catalog;

public record CatalogInvalidationDomain(CatalogEventDomain event, CatalogTypeDomain catalog, String code,
		String locale) {

	public CatalogInvalidationDomain {
		code = code.trim();
		locale = locale.trim();
	}

	public boolean hasEntry() {
		return !code.isBlank() && !locale.isBlank();
	}

	public boolean requiresEviction() {
		return event.invalidatesCache() && catalog != CatalogTypeDomain.UNKNOWN;
	}
}
