package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.List;

/**
 * Pide actualizar las vistas materializadas del modulo porque cambio la informacion del modulo de origen.
 */
public record DwellingsViewRefreshDomain(DwellingsViewSource source) {

	/** Modulo donde cambio la informacion, con las vistas de este modulo que dependen de el. */
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
