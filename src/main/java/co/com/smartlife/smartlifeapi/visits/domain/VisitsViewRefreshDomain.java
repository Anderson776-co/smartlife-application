package co.com.smartlife.smartlifeapi.visits.domain;

import java.util.List;

public record VisitsViewRefreshDomain(VisitsViewSource source) {

	public enum VisitsViewSource {
		USERS(List.of("visits.authorizator")),
		DWELLINGS(List.of("visits.property_visit")),
		RESIDENTS(List.of("visits.resident"));

		private final List<String> views;

		VisitsViewSource(final List<String> views) {
			this.views = views;
		}

		public List<String> views() {
			return views;
		}
	}

}
