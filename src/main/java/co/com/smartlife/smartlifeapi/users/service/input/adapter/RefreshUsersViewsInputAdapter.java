package co.com.smartlife.smartlifeapi.users.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.users.service.input.adapter.mapper.RefreshUsersViewsMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.RefreshUsersViewsDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.port.RefreshUsersViewsInputPort;
import co.com.smartlife.smartlifeapi.users.service.usecase.contract.RefreshUsersViewsUseCase;

@Service
public class RefreshUsersViewsInputAdapter implements RefreshUsersViewsInputPort {

	private final RefreshUsersViewsUseCase useCase;
	private final RefreshUsersViewsMapperInput mapper;

	public RefreshUsersViewsInputAdapter(final RefreshUsersViewsUseCase useCase,
			final RefreshUsersViewsMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final RefreshUsersViewsDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
