package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.AuthorizatorMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.PropertyVisitInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultAuthorizatorPropertiesInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultAuthorizatorPropertiesUseCase;

@Service
public class ConsultAuthorizatorPropertiesInputAdapter implements ConsultAuthorizatorPropertiesInputPort {

	private final ConsultAuthorizatorPropertiesUseCase useCase;
	private final AuthorizatorMapperInput mapper;
	private final PropertyVisitInputMapper propertyVisitInputMapper;

	public ConsultAuthorizatorPropertiesInputAdapter(final ConsultAuthorizatorPropertiesUseCase useCase, final AuthorizatorMapperInput mapper,
			final PropertyVisitInputMapper propertyVisitInputMapper) {
		this.useCase = useCase;
		this.mapper = mapper;
		this.propertyVisitInputMapper = propertyVisitInputMapper;
	}

	@Override
	public List<PropertyVisitDtoResponse> execute(final ListAuthorizatorPropertiesDtoRequest data) {
		return useCase.list(mapper.toDomain(data))
				.map(propertyVisitInputMapper::toDto)
				.stream()
				.toList();
	}

}
