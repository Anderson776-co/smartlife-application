package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.List;

/**
 * Pide actualizar las vistas materializadas del modulo porque cambio la informacion del modulo de origen.
 */
public record ResidentsViewRefreshDomain(ResidentsViewSource source) {

	/** Modulo donde cambio la informacion, con las vistas de este modulo que dependen de el. */
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
