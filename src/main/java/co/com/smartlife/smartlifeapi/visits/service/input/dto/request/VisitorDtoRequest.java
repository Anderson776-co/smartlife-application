package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.util.Locale;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record VisitorDtoRequest(String documentNumber, String firstName, String middleName,
		String lastName, String secondLastName, String email, String vehiclePlate) {
	
	public VisitorDtoRequest {
		documentNumber = TextHelper.getDefaultWithTrim(documentNumber);
		firstName = TextHelper.getDefaultWithTrim(firstName);
		middleName = TextHelper.getDefaultWithTrim(middleName);
		lastName = TextHelper.getDefaultWithTrim(lastName);
		secondLastName = TextHelper.getDefaultWithTrim(secondLastName);
		email = TextHelper.getDefaultWithTrim(email).toLowerCase(Locale.ROOT);
		vehiclePlate = TextHelper.getDefaultWithTrim(vehiclePlate).toUpperCase(Locale.ROOT);
	}
	
	public static VisitorDtoRequest empty() {
		final String empty = TextHelper.getDefault();
		return new VisitorDtoRequest(empty, empty, empty, empty, empty, empty, empty);
	}

}
