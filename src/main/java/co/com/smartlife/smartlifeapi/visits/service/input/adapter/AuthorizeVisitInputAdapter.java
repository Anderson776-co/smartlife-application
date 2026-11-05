package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationMapperResponseInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.AuthorizeVisitUseCase;

@Service
public class AuthorizeVisitInputAdapter implements AuthorizeVisitInputPort{

	private final AuthorizeVisitUseCase useCase;
	private final VisitAuthorizationMapperInput mapper;
	private final VisitAuthorizationMapperResponseInput mapperResponse;
	
	public AuthorizeVisitInputAdapter(final AuthorizeVisitUseCase useCase,
			final VisitAuthorizationMapperInput mapper, VisitAuthorizationMapperResponseInput mapperResponse) {
		this.useCase = useCase;
		this.mapper = mapper;
		this.mapperResponse = mapperResponse;
	}

	@Override
	public VisitAuthorizationDtoResponse execute(AuthorizeVisitDtoRequest data) {
		
		VisitAuthorizationDomain requestedAuthorization = mapper.toDomain(data);
		VisitAuthorizationDomain registeredAuthorization = useCase.execute(requestedAuthorization);
		
		return mapperResponse.toDto(registeredAuthorization);
	}

}
