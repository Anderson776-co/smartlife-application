package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.input.dto.response.SessionDto;
import co.com.smartlife.smartlifeapi.users.service.input.port.LoginInputPort;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.LoginUseCase;

@Service
public class LoginInputAdapter implements LoginInputPort{
	
	private final LoginUseCase useCase;
	
	public LoginInputAdapter(final LoginUseCase useCase) {
		this.useCase = useCase;
	}

	@Override
	public SessionDto execute(String data) {
		return useCase.execute(data);
	}

}
