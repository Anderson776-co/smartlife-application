package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

public record PropertyVisitDomain(UUID id, String location, UUID ownerId) {

}
