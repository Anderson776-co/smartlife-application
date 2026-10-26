package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationMapperService;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitInputDto;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;

@Service
public class AuthorizeVisitInputAdapter implements AuthorizeVisitInputPort{

	private final AuthorizeVisitUseCase useCase;
	private final VisitAuthorizationMapperService mapper;
	
	public AuthorizeVisitInputAdapter(final AuthorizeVisitUseCase useCase,
			final VisitAuthorizationMapperService mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public VisitAuthorizationDtoResponse execute(AuthorizeVisitInputDto data) {
		final VisitAuthorizationDomain domain = mapper.toDomain(data);
		return useCase.execute(domain);
	}

}
