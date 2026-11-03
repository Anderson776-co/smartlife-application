package co.com.smartlife.smartlifeapi.users.domain;

import java.util.UUID;

/** Un usuario tal como lo ve el dominio. Los objetos de dominio no cambian: para modificarlos se crea una copia. */
public record UserDomain(UUID id, String documentNumber, String firstName, String middleName, String lastName,
		String secondLastName, String email, Boolean emailConfirmed, Boolean emailConfirmedDefaultValue,
		Boolean active, String externalId, IdTypeDomain idType, UrbanizationUserDomain urbanization) {

	public UserDomain withActive(final Boolean newActive) {
		return new UserDomain(id, documentNumber, firstName, middleName, lastName, secondLastName, email,
				emailConfirmed, emailConfirmedDefaultValue, newActive, externalId, idType, urbanization);
	}
}
