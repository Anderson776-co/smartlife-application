package co.com.smartlife.smartlifeapi.visits.domain;

import java.util.List;

/**
 * Pide actualizar las vistas materializadas del modulo porque cambio la informacion del modulo de origen.
 */
public record VisitsViewRefreshDomain(VisitsViewSource source) {

	/** Modulo donde cambio la informacion, con las vistas de este modulo que dependen de el. */
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
