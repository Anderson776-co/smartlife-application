package co.com.smartlife.smartlifeapi.residents.service.usecase.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.residents.domain.ResidentsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.RefreshResidentsViewsUseCase;

@Service
public class RefreshResidentsViewsUseCaseImpl implements RefreshResidentsViewsUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshResidentsViewsUseCaseImpl.class);

	private final MaterializedViewRefresherOutputPort refresher;

	public RefreshResidentsViewsUseCaseImpl(final MaterializedViewRefresherOutputPort refresher) {
		this.refresher = refresher;
	}

	@Override
	public void execute(final ResidentsViewRefreshDomain input) {
		for (final String view : input.source().views()) {
			try {
				refresher.refresh(view);
			} catch (final RuntimeException exception) {
				LOGGER.error("No se pudo actualizar la vista {}", view, exception);
			}
		}
	}

}
