package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper.UserStatusMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.ChangeUserStatusDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.port.ChangeUserStatusInputPort;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.ChangeUserStatusUseCase;

@Service
public class ChangeUserStatusInputAdapter implements ChangeUserStatusInputPort {

	private final ChangeUserStatusUseCase useCase;
	private final UserStatusMapperInput mapper;

	public ChangeUserStatusInputAdapter(final ChangeUserStatusUseCase useCase, final UserStatusMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final ChangeUserStatusDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
