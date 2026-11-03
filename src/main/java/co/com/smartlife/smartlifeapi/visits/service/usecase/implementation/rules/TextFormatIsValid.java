package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.StringFormatIsValidSpecification;

/**
 * Un dato ingresado debe cumplir el formato. Si no lo cumple, se rechaza con el mensaje {@code <campo>_INVALID}
 * del catalogo. No decide si el dato es obligatorio: eso es de {@link TextIsPresent}.
 * Datos: el texto, la expresion regular y el codigo base del campo (por ejemplo {@code VISITOR_EMAIL}).
 */
public final class TextFormatIsValid implements Rule {

	private static final String INVALID_SUFFIX = "_INVALID";

	private static final Rule instance = new TextFormatIsValid();

	private TextFormatIsValid() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 3) {
			throw VisitRuleErrors.incompleteParameters("TEXT_FORMAT_IS_VALID");
		}

		final var text = (String) data[0];
		final var pattern = (String) data[1];
		final var fieldCode = (String) data[2];

		if (!new StringFormatIsValidSpecification(pattern).isSatisfiedBy(text)) {
			throw VisitRuleErrors.business(fieldCode + INVALID_SUFFIX);
		}
	}

}
