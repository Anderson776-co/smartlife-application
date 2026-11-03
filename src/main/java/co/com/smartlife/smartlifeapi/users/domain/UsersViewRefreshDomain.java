package co.com.smartlife.smartlifeapi.users.domain;

import java.util.List;

/**
 * Pide actualizar las vistas materializadas del modulo porque cambio la informacion del modulo de origen.
 */
public record UsersViewRefreshDomain(UsersViewSource source) {

	/** Modulo donde cambio la informacion, con las vistas de este modulo que dependen de el. */
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
