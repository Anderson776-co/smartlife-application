package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.crosscutting.specification.generics.IsStringPresentSpecification;

/**
 * Un dato obligatorio debe haberse ingresado. Si llega nulo o en blanco, se rechaza con el mensaje
 * {@code <campo>_REQUIRED} del catalogo. Es la unica regla que responde "falta el dato": que el formato sea
 * incorrecto lo responden {@link TextFormatIsValid} y {@link TextLengthIsValid} con {@code <campo>_INVALID}.
 * Datos: el texto y el codigo base del campo (por ejemplo {@code VISITOR_EMAIL}).
 */
public final class TextIsPresent implements Rule {

	private static final String REQUIRED_SUFFIX = "_REQUIRED";

	private static final Rule instance = new TextIsPresent();

	private TextIsPresent() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 2) {
			throw VisitRuleErrors.incompleteParameters("TEXT_IS_PRESENT");
		}

		final var text = (String) data[0];
		final var fieldCode = (String) data[1];

		if (!new IsStringPresentSpecification().isSatisfiedBy(text)) {
			throw VisitRuleErrors.business(fieldCode + REQUIRED_SUFFIX);
		}
	}

}
