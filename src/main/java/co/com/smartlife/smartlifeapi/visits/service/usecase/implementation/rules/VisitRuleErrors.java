package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;

public final class VisitRuleErrors {

	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";

	private VisitRuleErrors() {
	}

	public static SmartLifeException business(final String code) {
		return SmartLifeException.create(userMessage(code), technicalMessage(code));
	}

	public static SmartLifeException authorization(final String code) {
		return SmartLifeException.createAuthorization(userMessage(code), technicalMessage(code));
	}

	public static SmartLifeException incompleteParameters(final String ruleName) {
		return SmartLifeException.create(MessageCatalogProvider.getContent("USER_ERROR_TRYING_TO_MAKE_AN_OPERATION"),
				MessageCatalogProvider.getContent("INCOMPLETE_PARAMETERS_" + ruleName + "_RULE"));
	}

	private static String userMessage(final String code) {
		return MessageCatalogProvider.getContent(USER_PREFIX + code);
	}

	private static String technicalMessage(final String code) {
		return MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code);
	}

}
