package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationAccessInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.VerifyVisitAuthorizationAccessInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

@Service
public class VerifyVisitAuthorizationAccessInputAdapter implements VerifyVisitAuthorizationAccessInputPort {

	private final VerifyVisitAuthorizationAccessUseCase useCase;
	private final VisitAuthorizationAccessInputMapper mapper;

	public VerifyVisitAuthorizationAccessInputAdapter(final VerifyVisitAuthorizationAccessUseCase useCase,
			final VisitAuthorizationAccessInputMapper mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public Boolean execute(final VisitAuthorizationAccessDtoRequest data) {
		return useCase.execute(mapper.toDomain(data));
	}

}
