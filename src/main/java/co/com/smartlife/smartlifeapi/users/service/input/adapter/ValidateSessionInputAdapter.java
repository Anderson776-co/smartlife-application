package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.users.service.input.port.ValidateSessionInputPort;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.ValidateSessionUseCase;

@Service
public class ValidateSessionInputAdapter implements ValidateSessionInputPort{
	
	private final ValidateSessionUseCase useCase;
	
	

	public ValidateSessionInputAdapter(final ValidateSessionUseCase useCase) {
		this.useCase = useCase;
	}

	@Override
	public CurrentUser execute(String data) {
		return useCase.execute(data);
	}

}
