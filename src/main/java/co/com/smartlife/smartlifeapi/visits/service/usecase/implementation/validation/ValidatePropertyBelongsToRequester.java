package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.domain.validator.Validator;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.PropertyVisitDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.PropertyVisitRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitResidentRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitResidentEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.PropertyMapperUseCase;

@Component
public final class ValidatePropertyBelongsToRequester implements Validator{

	private final PropertyVisitRepository propertyVisitRepository;
	private final VisitResidentRepository visitResidentRepository;
	private final PropertyMapperUseCase mapperUseCase;
	
	public ValidatePropertyBelongsToRequester(final PropertyVisitRepository propertyVisitRepository,
			final VisitResidentRepository visitResidentRepository, final PropertyMapperUseCase mapperUseCase) {
		this.propertyVisitRepository = propertyVisitRepository;
		this.visitResidentRepository = visitResidentRepository;
		this.mapperUseCase = mapperUseCase;
	}

	@Override
	public void validate(Object... data) {
		
		if (!new IsMandatorySpecification().isSatisfiedBy(data)) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
			var technicalMessage = MessageCatalogProvider.getContent("NULL_PARAMETERS_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		if (data.length < 2) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");

            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
		final CurrentUser currentUser = (CurrentUser) data[0];
		final UUID propertyId = (UUID) data[1];
	}
	
	public PropertyVisitDomain findAuthorizedProperty(final CurrentUser requester, final UUID propertyId) {
		final  PropertyVisitEntity property = propertyVisitRepository.findById(propertyId)
		        .orElseThrow(() -> {
		            var userMessage = MessageCatalogProvider.getContent("VISIT_PROPERTY_NOT_FOUND");

		            var technicalMessage = MessageCatalogProvider.getContent("VISIT_PROPERTY_NOT_FOUND_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");

		            return SmartLifeException.createAuthentication(
		                userMessage, technicalMessage);
		        });
		
		if (!isOwner(requester, property) && !isResident(requester, property)) {
			var userMessage = MessageCatalogProvider.getContent("VISIT_PROPERTY_NOT_RELATED");
			var technicalMessage = MessageCatalogProvider.getContent("VISIT_NOT_RELATED_TO_PROPERTY_VALIDATE_PROPERTY_BELONGS_TO_REQUESTER");
			
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		return mapperUseCase.toDomain(property);
	}
	
	private static boolean isOwner(final CurrentUser requester, final PropertyVisitEntity property) {
		return requester.hasAnyRole(ValidateRequesterRole.OWNER)
				&& requester.userId().equals(property.getOwnerId());
	}
	
	private boolean isResident(final CurrentUser requester, final PropertyVisitEntity property) {
		return requester.hasAnyRole(ValidateRequesterRole.RESIDENT)
				&& visitResidentRepository.findById(requester.userId())
						.map(VisitResidentEntity::getProperty)
						.filter(residence -> property.getId().equals(residence.getId()))
						.isPresent();
	}

}
