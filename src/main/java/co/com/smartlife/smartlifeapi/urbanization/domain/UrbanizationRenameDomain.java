package co.com.smartlife.smartlifeapi.urbanization.domain;

import java.util.UUID;

/** Lo que se pide: cambiarle el nombre a una urbanizacion. */
public record UrbanizationRenameDomain(UUID urbanizationId, String name) {
}
