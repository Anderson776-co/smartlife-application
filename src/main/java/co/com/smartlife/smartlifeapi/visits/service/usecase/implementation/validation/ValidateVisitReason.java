package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.VisitReasonMapperUseCase;

@Component
public final class ValidateVisitReason implements Validator{

	private final VisitReasonRepository visitReasonRepository;
	private final VisitReasonMapperUseCase mapperUseCase;
	
	
	public ValidateVisitReason(final VisitReasonRepository visitReasonRepository,
			final VisitReasonMapperUseCase mapperUseCase) {
		this.visitReasonRepository = visitReasonRepository;
		this.mapperUseCase = mapperUseCase;
	}

	@Override
	public void validate(Object... data) {
		
		if (!new IsMandatorySpecification().isSatisfiedBy(data) || data.length < 1) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage =MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE");
            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
		final UUID visitReasonId = (UUID) data[0];
		findVisitReason(visitReasonId);
		
	}
	
	public VisitReasonDomain findVisitReason(final UUID visitReasonId) {
		
		IsUUIDPresentRule.executeRule(visitReasonId);
		
		final VisitReasonEntity visitReason = visitReasonRepository.findById(visitReasonId)
		        .orElseThrow(() -> {
		            var userMessage =
		                MessageCatalogProvider.getContent("USER_NOT_REGISTERED");

		            var technicalMessage =
		                MessageCatalogProvider.getContent(
		                    "USER_NOT_REGISTRED_TRY_TO_LOG_IN");

		            throw SmartLifeException.createAuthentication(
		                userMessage, technicalMessage);
		        });
		
		return mapperUseCase.toDomain(visitReason);
	}

}