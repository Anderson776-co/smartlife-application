package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.StringLengthIsValidSpecification;

/**
 * Un dato ingresado debe tener un largo permitido. Si no lo tiene, se rechaza con el mensaje {@code <campo>_INVALID}
 * del catalogo. No decide si el dato es obligatorio: eso es de {@link TextIsPresent}.
 * Datos: el texto, el largo minimo, el largo maximo y el codigo base del campo (por ejemplo {@code VISITOR_EMAIL}).
 */
public final class TextLengthIsValid implements Rule {

	private static final String INVALID_SUFFIX = "_INVALID";

	private static final Rule instance = new TextLengthIsValid();

	private TextLengthIsValid() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 4) {
			throw VisitRuleErrors.incompleteParameters("TEXT_LENGTH_IS_VALID");
		}

		final var text = (String) data[0];
		final var minLength = (Integer) data[1];
		final var maxLength = (Integer) data[2];
		final var fieldCode = (String) data[3];

		if (!new StringLengthIsValidSpecification(minLength, maxLength).isSatisfiedBy(text)) {
			throw VisitRuleErrors.business(fieldCode + INVALID_SUFFIX);
		}
	}

}
