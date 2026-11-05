package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitReasonInputMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultVisitReasonsInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitReasonsUseCase;

@Service
public class ConsultVisitReasonsInputAdapter implements ConsultVisitReasonsInputPort {

	private final ConsultVisitReasonsUseCase useCase;
	private final VisitReasonInputMapper mapper;

	public ConsultVisitReasonsInputAdapter(final ConsultVisitReasonsUseCase useCase,
			final VisitReasonInputMapper mapper) {
		this.useCase = useCase;
		this.mapper = mapper;
	}

	@Override
	public List<VisitReasonDtoResponse> execute() {
		return mapper.toDtoList(useCase.list());
	}

}
