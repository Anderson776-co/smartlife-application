package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.Arrays;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

/**
 * Si el motivo exige documento (por ejemplo servicio tecnico), el documento es obligatorio, de un tipo
 * permitido, dentro del tamano maximo y su contenido debe corresponder al tipo declarado.
 * Datos: {@link VisitReasonEntity}, {@link AttachedDocumentDomain} y {@link VisitParametersDTO}.
 */
public final class VisitDocumentMeetsReason implements Rule {

	private static final Rule instance = new VisitDocumentMeetsReason();

	private VisitDocumentMeetsReason() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 3) {
			throw VisitRuleErrors.incompleteParameters("VISIT_DOCUMENT_MEETS_REASON");
		}

		final var reason = (VisitReasonEntity) data[0];
		final var document = (AttachedDocumentDomain) data[1];
		final var parameters = (VisitParametersDTO) data[2];

		if (!Boolean.TRUE.equals(reason.getRequiresDocument())) {
			return;
		}
		if (document == null || !document.hasContent()) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_REQUIRED");
		}
		if (!parameters.documentAllowedTypes().contains(document.contentType()) || !matchesSignature(document)) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_TYPE_NOT_ALLOWED");
		}
		if (document.content().length > parameters.documentMaxSizeInBytes()) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_TOO_LARGE");
		}
	}

	/**
	 * El tipo viene del cliente; se contrasta con los primeros bytes que define el parametro {@code DOCUMENT_SIGNATURES}
	 * del {@link ParameterCatalog}. Un tipo permitido que no tenga firma es un error de configuracion y no se acepta
	 * a ciegas: se falla indicando cual falta, igual que cuando falta cualquier otro parametro.
	 */
	private static boolean matchesSignature(final AttachedDocumentDomain document) {
		final byte[] signature = ParameterCatalog.DOCUMENT_SIGNATURES.get(document.contentType());
		if (signature == null) {
			throw new IllegalStateException("El tipo de documento permitido '" + document.contentType()
					+ "' no tiene firma en smartlife.visits.validation.format.document-signatures");
		}
		return document.content().length >= signature.length
				&& Arrays.equals(Arrays.copyOf(document.content(), signature.length), signature);
	}

}
