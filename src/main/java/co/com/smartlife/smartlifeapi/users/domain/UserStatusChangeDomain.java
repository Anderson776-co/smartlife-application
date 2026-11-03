package co.com.smartlife.smartlifeapi.users.domain;

import java.util.UUID;

/** Lo que se pide: dejar al usuario activo o inactivo. `active` es nulo si no se indico. */
public record UserStatusChangeDomain(UUID userId, Boolean active) {
}
