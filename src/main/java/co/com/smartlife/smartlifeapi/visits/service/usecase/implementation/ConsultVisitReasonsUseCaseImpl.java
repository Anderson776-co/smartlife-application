package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitReasonsUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitReasonMapperUseCase;

@Service
public class ConsultVisitReasonsUseCaseImpl implements ConsultVisitReasonsUseCase {

	private final VisitReasonRepository visitReasonRepository;
	private final VisitReasonMapperUseCase mapperUseCase;

	public ConsultVisitReasonsUseCaseImpl(final VisitReasonRepository visitReasonRepository,
			final VisitReasonMapperUseCase mapperUseCase) {
		this.visitReasonRepository = visitReasonRepository;
		this.mapperUseCase = mapperUseCase;
	}

	@Override
	public List<VisitReasonDomain> list() {
		return visitReasonRepository.findAll()
				.stream()
				.map(mapperUseCase::toDomain)
				.toList();
	}

}
