package co.com.smartlife.smartlifeapi.dwellings.event;

import java.util.UUID;

/**
 * Se cambio la informacion de una vivienda. Otros modulos que guardan una vista de estos datos la actualizan al recibirlo.
 */
public record DwellingUpdatedEvent(UUID dwellingId) {

}
