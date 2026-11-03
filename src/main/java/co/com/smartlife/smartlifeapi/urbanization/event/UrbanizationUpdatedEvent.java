package co.com.smartlife.smartlifeapi.urbanization.event;

import java.util.UUID;

/**
 * Se cambio la informacion de una urbanizacion. Otros modulos que guardan una vista de estos datos la actualizan al recibirlo.
 */
public record UrbanizationUpdatedEvent(UUID urbanizationId) {

}
