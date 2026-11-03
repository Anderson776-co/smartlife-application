package co.com.smartlife.smartlifeapi.dwellings.service.usecase.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.service.output.port.views.MaterializedViewRefresherOutputPort;
import co.com.smartlife.smartlifeapi.dwellings.domain.DwellingsViewRefreshDomain;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.RefreshDwellingsViewsUseCase;

@Service
public class RefreshDwellingsViewsUseCaseImpl implements RefreshDwellingsViewsUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshDwellingsViewsUseCaseImpl.class);

	private final MaterializedViewRefresherOutputPort refresher;

	public RefreshDwellingsViewsUseCaseImpl(final MaterializedViewRefresherOutputPort refresher) {
		this.refresher = refresher;
	}

	@Override
	public void execute(final DwellingsViewRefreshDomain input) {
		for (final String view : input.source().views()) {
			try {
				refresher.refresh(view);
			} catch (final RuntimeException exception) {
				LOGGER.error("No se pudo actualizar la vista {}", view, exception);
			}
		}
	}

}
