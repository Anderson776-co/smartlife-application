package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.visits.domain.VisitsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.RefreshVisitsViewsUseCase;

/**
 * Actualiza las vistas del modulo que dependen del modulo donde cambio la informacion. Si una vista no se
 * puede actualizar se deja el error en el log y se sigue con las demas: el cambio original ya esta guardado.
 */
@Service
public class RefreshVisitsViewsUseCaseImpl implements RefreshVisitsViewsUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshVisitsViewsUseCaseImpl.class);

	private final MaterializedViewRefresherOutputPort refresher;

	public RefreshVisitsViewsUseCaseImpl(final MaterializedViewRefresherOutputPort refresher) {
		this.refresher = refresher;
	}

	@Override
	public void execute(final VisitsViewRefreshDomain input) {
		for (final String view : input.source().views()) {
			try {
				refresher.refresh(view);
			} catch (final RuntimeException exception) {
				LOGGER.error("No se pudo actualizar la vista {}", view, exception);
			}
		}
	}

}
