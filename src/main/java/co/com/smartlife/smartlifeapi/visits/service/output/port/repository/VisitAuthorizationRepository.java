package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public interface VisitAuthorizationRepository extends CreateRepository<VisitAuthorizationEntity>, ReadRepository<UUID, VisitAuthorizationEntity>, 
UpdateRepository<UUID, VisitAuthorizationEntity>{
	
	Optional<VisitAuthorizationEntity> findActiveAuthorizationByVisitorId(UUID visitorId, Instant referencedDate);

	/** Hay una autorizacion activa del visitante en la propiedad que se cruza con el rango dado. */
	boolean existsOverlappingAuthorization(UUID visitorId, UUID propertyId, Instant startDate, Instant endDate);

    /** Autorizaciones creadas por un autorizador, de la mas reciente a la mas antigua. */
    List<VisitAuthorizationEntity> findByAuthorizatorId(UUID authorizatorId);

    /** Una autorizacion, solo si fue creada por ese autorizador. */
    Optional<VisitAuthorizationEntity> findByIdAndAuthorizatorId(UUID id, UUID authorizatorId);
}
