package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.UUID;

/** Un usuario que puede ser propietario de una vivienda. */
public record UserOwnerDomain(UUID id, String documentNumber, String fullName, Boolean active, UUID urbanizationId) {
}
