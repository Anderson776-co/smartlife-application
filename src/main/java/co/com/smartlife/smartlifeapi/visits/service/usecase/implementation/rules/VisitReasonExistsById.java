package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitReasonRepository;

public class VisitReasonExistsById implements Rule{
	
	private static final Rule instance = new VisitReasonExistsById();
	
	private VisitReasonExistsById() {
	}
	
	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(Object... data) {
		if(data.length < 2) {
			var userMessage = MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION");
			var technicalMessage = MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_VISIT_REASON_EXISTS_BY_ID_RULE");
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
		var id = (UUID) data[0];
		var visitReasonRepository = (VisitReasonRepository) data[1];
		
		if (visitReasonRepository.findById(id).isEmpty()) {
			var userMessage = MessageCatalogProvider.getContent("USER_MESSAGE__VISIT_REASON_DOESNT_EXISTS_BY_ID");
			var technicalMessage = MessageCatalogProvider.getContent("TECHNICAL_MESSAGE__VISIT_REASON_DOESNT_EXISTS_BY_ID");
			throw SmartLifeException.create(userMessage, technicalMessage);
		}
		
	}
	

}
