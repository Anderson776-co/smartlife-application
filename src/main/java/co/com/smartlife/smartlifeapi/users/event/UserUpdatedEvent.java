package co.com.smartlife.smartlifeapi.users.event;

import java.util.UUID;

/**
 * Se cambio la informacion de un usuario. Otros modulos que guardan una vista de estos datos la actualizan al recibirlo.
 */
public record UserUpdatedEvent(UUID userId) {

}
