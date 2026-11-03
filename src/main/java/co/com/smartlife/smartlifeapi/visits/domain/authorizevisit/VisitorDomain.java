package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

public record VisitorDomain(UUID id, String documentNumber, String firstName, String middleName,
		String lastName, String secondLastName, String email, String vehiclePlate) {

	public VisitorDomain withId(final UUID newId) {
		return new VisitorDomain(newId, documentNumber, firstName, middleName, lastName, secondLastName, email,
				vehiclePlate);
	}
}
