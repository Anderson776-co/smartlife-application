package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.Arrays;
import java.util.Map;

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

	private static final Map<String, byte[]> SIGNATURES = Map.of(
			"application/pdf", new byte[] { '%', 'P', 'D', 'F' },
			"image/jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF },
			"image/png", new byte[] { (byte) 0x89, 'P', 'N', 'G' });

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

	/** El tipo viene del cliente; se contrasta con los primeros bytes. Un tipo sin firma conocida se acepta. */
	private static boolean matchesSignature(final AttachedDocumentDomain document) {
		final byte[] signature = SIGNATURES.get(document.contentType());
		return signature == null
				|| Arrays.equals(Arrays.copyOf(document.content(), signature.length), signature);
	}

}
