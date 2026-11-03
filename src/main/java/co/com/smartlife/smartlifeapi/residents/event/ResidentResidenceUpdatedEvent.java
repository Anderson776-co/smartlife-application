package co.com.smartlife.smartlifeapi.residents.event;

import java.util.UUID;

public record ResidentResidenceUpdatedEvent(UUID residentResidenceId, UUID residentId) {

}
