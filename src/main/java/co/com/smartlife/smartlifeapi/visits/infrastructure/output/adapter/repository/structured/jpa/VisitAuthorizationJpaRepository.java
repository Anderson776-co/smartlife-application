package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;

public interface VisitAuthorizationJpaRepository extends JpaRepository<VisitAuthorizationJpaEntity, UUID>{
	boolean existsOverlapping(
			UUID visitorId, UUID propertyId, Instant newEndDate, Instant newStartDate);
}
