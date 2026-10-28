package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;

public interface VisitAuthorizationJpaRepository extends JpaRepository<VisitAuthorizationJpaEntity, UUID>{
	Optional<VisitAuthorizationJpaEntity> findActiveAuthorizationByVisitorId(
		UUID visitorId, Instant referenceDate);
}
