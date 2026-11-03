package co.com.smartlife.smartlifeapi.residents.event;

import java.util.UUID;

/**
 * Se cambio la relacion entre un residente y una residencia. Otros modulos que guardan una vista de estos datos la actualizan al recibirlo.
 */
public record ResidentResidenceUpdatedEvent(UUID residentResidenceId, UUID residentId) {

}
