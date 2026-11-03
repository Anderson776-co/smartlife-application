package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.UUID;

/** Lo que se pide: cambiar el propietario de una vivienda. */
public record DwellingOwnerChangeDomain(UUID dwellingId, UUID ownerId) {
}
