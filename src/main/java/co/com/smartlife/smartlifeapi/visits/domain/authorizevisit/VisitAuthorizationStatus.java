package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

public enum VisitAuthorizationStatus {
	UPCOMING, CURRENT, EXPIRED, CANCELLED, UNSPECIFIED;

	public boolean isSpecified() {
		return this != UNSPECIFIED;
	}
}
