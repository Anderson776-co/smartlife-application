package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import java.util.Locale;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitorMapperUseCase;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateNewVisitorData;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitorDocumentNumber;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation.ValidateVisitorEmailIsNotRegistered;

@Component
public class ResolveVisitor {

	private final VisitorRepository visitorRepository;
	private final VisitorMapperUseCase mapper;

	public ResolveVisitor(final VisitorRepository visitorRepository, final VisitorMapperUseCase mapper) {
		this.visitorRepository = visitorRepository;
		this.mapper = mapper;
	}

	public VisitorResolution resolve(final VisitorDomain data) {
		ValidateVisitorDocumentNumber.executeValidation(data.documentNumber());

		final String documentNumber = TextHelper.getDefaultWithTrim(data.documentNumber());

		return visitorRepository.findByDocumentNumber(documentNumber)
				.map(VisitorResolution::existing)
				.orElseGet(() -> VisitorResolution.created(buildNewVisitor(documentNumber, data)));
	}

	private VisitorEntity buildNewVisitor(final String documentNumber, final VisitorDomain data) {
		ValidateNewVisitorData.executeValidation(data);

		final String email = TextHelper.getDefaultWithTrim(data.email()).toLowerCase(Locale.ROOT);
		ValidateVisitorEmailIsNotRegistered.executeValidation(email, visitorRepository);

		return mapper.toEntity(data.withId(UUIDHelper.getUUIDHelper().generateNewUUID()));
	}

}
