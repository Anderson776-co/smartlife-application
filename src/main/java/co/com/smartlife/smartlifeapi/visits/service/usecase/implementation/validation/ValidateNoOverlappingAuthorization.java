package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import java.time.Instant;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitAuthorizationRepository;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.VisitorResolution;

@Component
public final class ValidateNoOverlappingAuthorization implements Validator{
	private final VisitAuthorizationRepository visitAuthorizationRepository;

	public ValidateNoOverlappingAuthorization(final VisitAuthorizationRepository visitAuthorizationRepository) {
		this.visitAuthorizationRepository = visitAuthorizationRepository;
	}

	@Override
	public void validate(Object... data) {
		if (!new IsMandatorySpecification().isSatisfiedBy(data)) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
			var technicalMessage = MessageCatalogProvider.getContent("NULL_PARAMETERS_VALIDATE_VISIT_REASON");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		if (data.length < 2) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");

            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
		VisitorResolution visitorResolution = (VisitorResolution) data[0];
		Instant now = (Instant) data[1];
		
		validateVisitorAuthorization(visitorResolution, now);
		
	}
	
	public void validateVisitorAuthorization(final VisitorResolution visitor, final Instant now) {
		if (visitor.isNew()) {
			return;   // un visitante nuevo no tiene autorizaciones previas
		}
		
		visitAuthorizationRepository.findActiveAuthorizationByVisitorId(visitor.entity().getId(), now)
	    .ifPresent(active -> {
	        var userMessage = MessageCatalogProvider.getContent("VISITOR_HAS_ACTIVE_AUTHORIZATION");

	        var technicalMessage = MessageCatalogProvider.getContent(
	            "VISITOR_HAS_ACTIVE_AUTHORIZATION_VALIDATE_VISITOR_HAS_ANOTHER_ACTIVE_AUTHORIZATION"
	        );

	        throw SmartLifeException.create(userMessage, technicalMessage);
	    });
	}
	
	

}
