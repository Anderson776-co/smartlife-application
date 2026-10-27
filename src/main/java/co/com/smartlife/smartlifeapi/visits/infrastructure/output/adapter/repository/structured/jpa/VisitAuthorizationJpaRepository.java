package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;

public interface VisitAuthorizationJpaRepository extends JpaRepository<VisitAuthorizationJpaEntity, UUID> {

	/**
	 * Indica si el visitante ya tiene una autorizacion para esa propiedad que se
	 * solapa con el rango dado: la existente empieza antes de que termine el rango
	 * nuevo y termina despues de que empieza.
	 */
	@Query("""
			SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
			FROM VisitAuthorizationJpaEntity v
			WHERE v.visitor.id = :visitorId
			  AND v.property.id = :propertyId
			  AND v.authorizationStartDate < :newEndDate
			  AND v.authorizationEndDate > :newStartDate
			""")
	boolean existsOverlapping(@Param("visitorId") UUID visitorId, @Param("propertyId") UUID propertyId,
			@Param("newEndDate") Instant newEndDate, @Param("newStartDate") Instant newStartDate);
}