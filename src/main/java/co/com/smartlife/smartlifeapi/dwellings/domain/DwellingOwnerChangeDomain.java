package co.com.smartlife.smartlifeapi.dwellings.domain;

import java.util.UUID;

public record DwellingOwnerChangeDomain(UUID dwellingId, UUID ownerId) {
}
