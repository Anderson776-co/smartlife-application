package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.time.Instant;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public interface VisitAuthorizationRepository extends CreateRepository<VisitAuthorizationEntity>, ReadRepository<UUID, VisitAuthorizationEntity>, 
UpdateRepository<UUID, VisitAuthorizationEntity>{
	boolean existsOverlapping(UUID visitorId, UUID propertyId, Instant startDate, Instant endDate);
}
