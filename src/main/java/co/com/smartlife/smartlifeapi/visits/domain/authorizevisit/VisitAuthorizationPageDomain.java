package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.List;

/**
 * Una pagina de resultados de la consulta de autorizaciones. La pagina empieza en 0.
 * Los valores fuera de rango no se rechazan: se ajustan (pagina minima 0, tamano entre 1 y {@link #MAX_SIZE},
 * y {@link #DEFAULT_SIZE} si no es positivo). Pedir una pagina mas alla de la ultima devuelve el contenido vacio
 * con los totales reales.
 */
public record VisitAuthorizationPageDomain(List<VisitAuthorizationSummaryDomain> content, int page, int size,
		long totalElements, int totalPages) {

	public static final int DEFAULT_SIZE = 10;
	public static final int MAX_SIZE = 50;

	public VisitAuthorizationPageDomain {
		content = content == null ? List.of() : List.copyOf(content);
	}

	/** Toma de la lista completa (ya filtrada y ordenada) solo los elementos de la pagina pedida. */
	public static VisitAuthorizationPageDomain of(final List<VisitAuthorizationSummaryDomain> all, final int page,
			final int size) {
		final List<VisitAuthorizationSummaryDomain> source = all == null ? List.of() : all;
		final int safePage = Math.max(page, 0);
		final int safeSize = size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
		final long total = source.size();
		final int totalPages = (int) ((total + safeSize - 1) / safeSize);
		final long from = (long) safePage * safeSize;
		final List<VisitAuthorizationSummaryDomain> content = from >= total ? List.of()
				: source.subList((int) from, (int) Math.min(from + safeSize, total));
		return new VisitAuthorizationPageDomain(content, safePage, safeSize, total, totalPages);
	}
}
