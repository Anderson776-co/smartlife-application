package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;

@Component
public final class ValidateAttachedDocument implements Validator{

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
		
		final VisitReasonDomain visitReason = (VisitReasonDomain) data [0];
		final AttachedDocumentDtoRequest attachedDocument = (AttachedDocumentDtoRequest) data[1];
		
		validateRequieresDocument(visitReason, attachedDocument);
		validateDocumentLength(attachedDocument);
	}
	
	public void validateRequieresDocument(final VisitReasonDomain reason, final AttachedDocumentDtoRequest document) {
		if (Boolean.TRUE.equals(reason.requiresDocument())) {
			var userMessage = MessageCatalogProvider.getContent("VISIT_DOCUMENT_REQUIRED");
			var technicalMessage = MessageCatalogProvider.getContent("VISIT_DOCUMENT_REQUIRED");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
	}
	
	public void validateDocumentLength(final AttachedDocumentDtoRequest document) {
		// Pendiente
	}
	
	

}
