package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.List;

public record ResidentsViewRefreshDomain(ResidentsViewSource source) {

	public enum ResidentsViewSource {
		USERS(List.of("residents.user_resident")),
		DWELLINGS(List.of("residents.residence"));

		private final List<String> views;

		ResidentsViewSource(final List<String> views) {
			this.views = views;
		}

		public List<String> views() {
			return views;
		}
	}

}
