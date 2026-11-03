package co.com.smartlife.smartlifeapi.shared.domain.catalog;

public enum CatalogEventDomain {
	PUBLISH,
	UNPUBLISH,
	DELETE,
	OTHER;

	public boolean invalidatesCache() {
		return this != OTHER;
	}
}
