package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import java.util.UUID;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.input.port.LogOutInputPort;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.LogOutUseCase;

@Service
public class LogOutInputAdapter implements LogOutInputPort{

	private final LogOutUseCase useCase;
	
	public LogOutInputAdapter(final LogOutUseCase useCase) {
		this.useCase = useCase;
	}

	@Override
	public void execute(UUID data) {
		useCase.execute(data);
	}

}
