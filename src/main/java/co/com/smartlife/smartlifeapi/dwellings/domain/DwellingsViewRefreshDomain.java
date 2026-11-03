package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.List;

public record DwellingsViewRefreshDomain(DwellingsViewSource source) {

	public enum DwellingsViewSource {
		USERS(List.of("dwellings.user_owner")),
		URBANIZATION(List.of("dwellings.urbanization"));

		private final List<String> views;

		DwellingsViewSource(final List<String> views) {
			this.views = views;
		}

		public List<String> views() {
			return views;
		}
	}

}
