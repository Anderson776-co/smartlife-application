package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsMandatorySpecification;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.AuthorizatorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper.AuthorizatorMapperUseCase;

@Component
public final class ValidateActiveAuthorizator implements Validator{
	
	private final AuthorizatorRepository authorizatorRepository;
	private final AuthorizatorMapperUseCase mapperUseCase;

	public ValidateActiveAuthorizator(final AuthorizatorRepository authorizatorRepository, final AuthorizatorMapperUseCase mapperUseCase) {
		this.authorizatorRepository = authorizatorRepository;
		this.mapperUseCase = mapperUseCase;
	}

	@Override
	public void validate(Object... data) {
		
		if (!new IsMandatorySpecification().isSatisfiedBy(data) || data.length < 1) {
            var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
            var technicalMessage =MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VALIDATE");
            throw SmartLifeException.create(userMessage, technicalMessage);
        }
		
		UUID userId = (UUID) data[0];
		findActiveAuthorizator(userId);
	}
	
	public AuthorizatorDomain findActiveAuthorizator(final UUID userId) {
		AuthorizatorEntity authorizator = authorizatorRepository.findById(userId)
        .orElseThrow(() -> {
            var userMessage =
                MessageCatalogProvider.getContent("USER_NOT_REGISTERED");

            var technicalMessage =
                MessageCatalogProvider.getContent(
                    "USER_NOT_REGISTRED_TRY_TO_LOG_IN");

            throw SmartLifeException.createAuthentication(
                userMessage, technicalMessage);
        });
		
		if (!Boolean.TRUE.equals(authorizator.getIsActive())) {
			var userMessage = MessageCatalogProvider.getContent("VISIT_AUTHORIZATOR_INACTIVE");
			var technicalMessage = MessageCatalogProvider.getContent("VISIT_INACTIVE_TRY_TO_AUTHORIZE_A_VISIT");
			throw SmartLifeException.createAuthentication(userMessage, technicalMessage);
		}
		
		return mapperUseCase.toDomain(authorizator);
	}

}
