package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.validation;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.shared.service.usecase.Validator;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextFormatIsValid;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextIsPresent;
import co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules.TextLengthIsValid;

/**
 * El numero de documento es obligatorio. Primero se pregunta si se ingreso ({@code VISITOR_DOCUMENT_REQUIRED}) y
 * despues si su formato y su largo son correctos ({@code VISITOR_DOCUMENT_INVALID}); el formato y el largo salen de
 * los parametros {@code DOCUMENT_NUMBER_*} del {@link ParameterCatalog}.
 * Dato: el numero de documento; si no llega, se trata como no ingresado.
 */
public final class ValidateVisitorDocumentNumber implements Validator {

	static final String FIELD = "VISITOR_DOCUMENT";

	private static final Validator instance = new ValidateVisitorDocumentNumber();

	private ValidateVisitorDocumentNumber() {
	}

	public static void executeValidation(final Object... data) {
		instance.validate(data);
	}

	@Override
	public void validate(final Object... data) {
		final String documentNumber = data.length > 0 ? (String) data[0] : null;

		TextIsPresent.executeRule(documentNumber, FIELD);
		TextFormatIsValid.executeRule(documentNumber, ParameterCatalog.DOCUMENT_NUMBER_PATTERN, FIELD);
		TextLengthIsValid.executeRule(documentNumber, ParameterCatalog.DOCUMENT_NUMBER_MIN_LENGTH,
				ParameterCatalog.DOCUMENT_NUMBER_MAX_LENGTH, FIELD);
	}

}
