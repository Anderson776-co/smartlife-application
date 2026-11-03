package co.com.smartlife.smartlifeapi.residents.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.residents.service.input.adapter.mapper.ResidentResidenceStatusMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.ChangeResidentResidenceStatusDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.input.port.ChangeResidentResidenceStatusInputPort;
import co.com.smartlife.smartlifeapi.residents.service.usecase.contract.ChangeResidentResidenceStatusUseCase;

@Service
public class ChangeResidentResidenceStatusInputAdapter implements ChangeResidentResidenceStatusInputPort {

	private final ChangeResidentResidenceStatusUseCase useCase;
	private final ResidentResidenceStatusMapperInput mapper;

	public ChangeResidentResidenceStatusInputAdapter(final ChangeResidentResidenceStatusUseCase useCase, final ResidentResidenceStatusMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final ChangeResidentResidenceStatusDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
