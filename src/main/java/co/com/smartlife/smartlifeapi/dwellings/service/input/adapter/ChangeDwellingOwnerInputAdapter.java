package co.com.smartlife.smartlifeapi.dwellings.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.dwellings.service.input.adapter.mapper.DwellingOwnerMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.ChangeDwellingOwnerInputPort;
import co.com.smartlife.smartlifeapi.dwellings.service.usecase.contract.ChangeDwellingOwnerUseCase;

@Service
public class ChangeDwellingOwnerInputAdapter implements ChangeDwellingOwnerInputPort {

	private final ChangeDwellingOwnerUseCase useCase;
	private final DwellingOwnerMapperInput mapper;

	public ChangeDwellingOwnerInputAdapter(final ChangeDwellingOwnerUseCase useCase, final DwellingOwnerMapperInput mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public void execute(final ChangeDwellingOwnerDtoRequest data) {
		useCase.execute(mapper.toDomain(data));
	}

}
