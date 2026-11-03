package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.rules;

import java.util.Arrays;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.rules.Rule;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;

public final class VisitDocumentMeetsReason implements Rule {

	private static final Rule instance = new VisitDocumentMeetsReason();
	private static final byte[] NO_SIGNATURE = new byte[0];

	private VisitDocumentMeetsReason() {
	}

	public static void executeRule(final Object... data) {
		instance.execute(data);
	}

	@Override
	public void execute(final Object... data) {
		if (data.length < 2) {
			throw VisitRuleErrors.incompleteParameters("VISIT_DOCUMENT_MEETS_REASON");
		}

		final var reason = (VisitReasonEntity) data[0];
		final var document = ObjectHelper.getDefault((AttachedDocumentDomain) data[1], AttachedDocumentDomain.empty());

		if (!Boolean.TRUE.equals(reason.getRequiresDocument())) {
			return;
		}
		if (!document.hasContent()) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_REQUIRED");
		}
		if (!ParameterCatalog.DOCUMENT_ALLOWED_TYPES.contains(document.contentType()) || !matchesSignature(document)) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_TYPE_NOT_ALLOWED");
		}
		if (document.content().length > ParameterCatalog.DOCUMENT_MAX_SIZE_IN_BYTES) {
			throw VisitRuleErrors.business("VISIT_DOCUMENT_TOO_LARGE");
		}
	}

	private static boolean matchesSignature(final AttachedDocumentDomain document) {
		final byte[] signature = ObjectHelper.getDefault(ParameterCatalog.DOCUMENT_SIGNATURES.get().get(document.contentType()),
				NO_SIGNATURE);
		if (signature.length == 0) {
			throw new IllegalStateException("El tipo de documento permitido '" + document.contentType()
					+ "' no tiene firma en smartlife.visits.validation.format.document-signatures");
		}
		return document.content().length >= signature.length
				&& Arrays.equals(Arrays.copyOf(document.content(), signature.length), signature);
	}

}
