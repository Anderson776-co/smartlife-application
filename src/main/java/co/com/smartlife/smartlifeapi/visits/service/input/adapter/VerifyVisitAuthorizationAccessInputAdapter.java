package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitAuthorizationAccessDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.port.VerifyVisitAuthorizationAccessInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.VerifyVisitAuthorizationAccessUseCase;

@Service
public class VerifyVisitAuthorizationAccessInputAdapter implements VerifyVisitAuthorizationAccessInputPort {

	private final VerifyVisitAuthorizationAccessUseCase useCase;
	private final VisitAuthorizationMapperInput mapper;

	public VerifyVisitAuthorizationAccessInputAdapter(final VerifyVisitAuthorizationAccessUseCase useCase,
			final VisitAuthorizationMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public Boolean execute(final VisitAuthorizationAccessDtoRequest data) {
		final AuthorizatorDomain authorizator = mapper.toAuthorizatorDomain(data.requester());
		return useCase.execute(new VisitAuthorizationAccessDomain(authorizator, data.propertyId()));
	}

}
