package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import java.util.Locale;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.VisitorRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

@Component
public class ResolveVisitor {
	
	private static final String DOCUMENT_PATTERN = "^[A-Za-z0-9-]{4,30}$";
	private static final String NAME_PATTERN = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü' ]{1,100}$";
	private static final String PLATE_PATTERN = "^[A-Z0-9-]{3,20}$";
	private static final int EMAIL_MAX_LENGTH = 255;
	private static final String USER_PREFIX = "USER_MESSAGE_";
	private static final String TECHNICAL_PREFIX = "TECHNICAL_MESSAGE_";
	private final VisitorRepository visitorRepository;
	
	public ResolveVisitor(final VisitorRepository visitorRepository) {
		this.visitorRepository = visitorRepository;
	}
	
	public VisitorResolution resolve (final VisitorDomain data) {
		if (ObjectHelper.isNull(data) || TextHelper.isEmptyWithTrim(data.documentNumber())) {
			throw catalogError("VISITOR_DOCUMENT_REQUIRED");
		}
		
		final String documentNumber = TextHelper.getDefaultWithTrim(data.documentNumber());
		
		if (!TextHelper.formatIsValid(documentNumber, DOCUMENT_PATTERN, true)) {
			throw catalogError("VISITOR_DOCUMENT_INVALID");
		}
		
		return visitorRepository.findByDocumentNumber(documentNumber)
				.map(VisitorResolution::existing)
				.orElseGet(() -> VisitorResolution.created(buildNewVisitor(documentNumber, data)));
	}
	
	private VisitorEntity buildNewVisitor(final String documentNumber, final VisitorDomain data) {
		final String firstName = requiredName(data.firstName(), "VISITOR_FIRST_NAME_REQUIRED", "VISITOR_FIRST_NAME_INVALID");
		final String lastName = requiredName(data.lastName(), "VISITOR_LAST_NAME_REQUIRED", "VISITOR_LAST_NAME_INVALID");
		final String middleName = optionalName(data.middleName(), "VISITOR_MIDDLE_NAME_INVALID");
		final String secondLastName = optionalName(data.secondLastName(), "VISITOR_SECOND_LAST_NAME_INVALID");

		final String email = TextHelper.getDefaultWithTrim(data.email()).toLowerCase(Locale.ROOT);
		if (email.isEmpty()) {
			throw catalogError("VISITOR_EMAIL_REQUIRED");
		}
		if (!TextHelper.isValidEmail(email) || email.length() > EMAIL_MAX_LENGTH) {
			throw catalogError("VISITOR_EMAIL_INVALID");
		}
		final String plate = TextHelper.getDefaultWithTrim(data.vehiclePlate()).toUpperCase(Locale.ROOT);
		if (!plate.isEmpty() && !plate.matches(PLATE_PATTERN)) {
			throw catalogError("VISITOR_PLATE_INVALID");
		}
		
		if (visitorRepository.findByEmail(email).isPresent()) {
			throw catalogError("VISITOR_EMAIL_ALREADY_REGISTERED");
		}
		return new VisitorEntity(UUIDHelper.getUUIDHelper().generateNewUUID(), firstName, middleName,
				lastName, secondLastName, documentNumber, email, plate.isEmpty() ? null : plate);
	}

	/** Un nombre obligatorio tiene dos rechazos distintos: vacio (REQUIRED) y con formato incorrecto (INVALID). */
	private static String requiredName(final String value, final String requiredCode, final String invalidCode) {
		final String name = TextHelper.getDefaultWithTrim(value);
		if (name.isEmpty()) {
			throw catalogError(requiredCode);
		}
		if (!name.matches(NAME_PATTERN)) {
			throw catalogError(invalidCode);
		}
		return name;
	}

	/** Un nombre opcional solo se rechaza si viene con formato incorrecto. */
	private static String optionalName(final String value, final String invalidCode) {
		final String name = TextHelper.getDefaultWithTrim(value);
		if (name.isEmpty()) {
			return name;
		}
		if (!name.matches(NAME_PATTERN)) {
			throw catalogError(invalidCode);
		}
		return name;
	}

	/** Mensaje de usuario (USER_MESSAGE_X) y mensaje técnico (TECHNICAL_MESSAGE_X), ambos del catálogo de Strapi. */
	private static SmartLifeException catalogError(final String code) {
		return SmartLifeException.create(MessageCatalogProvider.getContent(USER_PREFIX + code),
				MessageCatalogProvider.getContent(TECHNICAL_PREFIX + code));
	}

}
