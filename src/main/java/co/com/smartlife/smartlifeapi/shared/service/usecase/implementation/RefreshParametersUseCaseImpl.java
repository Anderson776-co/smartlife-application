package co.com.smartlife.smartlifeapi.shared.service.usecase.implementation;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeDomain;
import co.com.smartlife.smartlifeapi.shared.domain.parameters.ParametersChangeEventDomain;
import co.com.smartlife.smartlifeapi.shared.service.output.port.parameters.ParametersRefreshOutputPort;
import co.com.smartlife.smartlifeapi.shared.service.usecase.contract.RefreshParametersUseCase;

/** Recarga los parametros cuando se hace push a la rama vigilada del repositorio de parametros. */
@Service
public class RefreshParametersUseCaseImpl implements RefreshParametersUseCase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefreshParametersUseCaseImpl.class);

	private final ParametersRefreshOutputPort parametersRefresh;
	private final String watchedReference;

	public RefreshParametersUseCaseImpl(final ParametersRefreshOutputPort parametersRefresh,
			@Value("${smartlife.webhooks.config-ref:refs/heads/main}") final String watchedReference) {
		this.parametersRefresh = parametersRefresh;
		this.watchedReference = watchedReference;
	}

	@Override
	public void execute(final ParametersChangeDomain input) {
		if (input.event() != ParametersChangeEventDomain.PUSH || !watchedReference.equals(input.reference())) {
			LOGGER.debug("Webhook de parámetros ignorado: evento {} o rama no vigilada", input.event());
			return;
		}

		final Set<String> changedKeys = parametersRefresh.refresh();
		LOGGER.info("Parámetros recargados desde el servidor de configuración: {} llaves cambiadas {}",
				changedKeys.size(), changedKeys);
	}
}
