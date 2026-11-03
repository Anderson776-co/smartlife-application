package co.com.smartlife.smartlifeapi.users.domain;

import java.util.List;

public record UsersViewRefreshDomain(UsersViewSource source) {

	public enum UsersViewSource {
		URBANIZATION(List.of("users.urbanization"));

		private final List<String> views;

		UsersViewSource(final List<String> views) {
			this.views = views;
		}

		public List<String> views() {
			return views;
		}
	}

}
