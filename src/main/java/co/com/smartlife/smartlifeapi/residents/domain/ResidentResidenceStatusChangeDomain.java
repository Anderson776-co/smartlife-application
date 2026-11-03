package co.com.smartlife.smartlifeapi.residents.domain;

import java.util.UUID;

/** Lo que se pide: activar o desactivar la relacion de un residente con una residencia. */
public record ResidentResidenceStatusChangeDomain(UUID residentResidenceId, Boolean active) {
}
