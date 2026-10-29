package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;

public interface PropertyVisitRepository extends ReadRepository<UUID, PropertyVisitEntity>{
	
	boolean isOwnedBy(UUID propertyId, UUID ownerId);
	
}
