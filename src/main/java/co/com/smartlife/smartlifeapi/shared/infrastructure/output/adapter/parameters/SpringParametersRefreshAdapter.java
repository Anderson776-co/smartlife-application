package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.parameters;

import java.util.Set;

import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.shared.service.output.port.parameters.ParametersRefreshOutputPort;

@Component
public class SpringParametersRefreshAdapter implements ParametersRefreshOutputPort {

	private final ContextRefresher contextRefresher;

	public SpringParametersRefreshAdapter(final ContextRefresher contextRefresher) {
		this.contextRefresher = contextRefresher;
	}

	@Override
	public Set<String> refresh() {
		return contextRefresher.refresh();
	}
}
