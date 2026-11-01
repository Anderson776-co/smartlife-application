package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitAuthorizationJpaEntity;

public interface VisitAuthorizationJpaRepository extends JpaRepository<VisitAuthorizationJpaEntity, UUID>{

	/**
	 * Autorizacion activa del visitante: marcada como activa y que todavia no ha
	 * vencido en la fecha de referencia. Si hubiera mas de una, Spring lanza un error.
	 */
	@Query("""
			SELECT v FROM VisitAuthorizationJpaEntity v
			WHERE v.visitor.id = :visitorId
			  AND v.isActive = true
			  AND v.authorizationEndDate > :referenceDate
			""")
	Optional<VisitAuthorizationJpaEntity> findActiveAuthorizationByVisitorId(
			@Param("visitorId") UUID visitorId, @Param("referenceDate") Instant referenceDate);

	/** Dos rangos se cruzan cuando cada uno empieza antes de que el otro termine. */
	@Query("""
			SELECT COUNT(v) > 0 FROM VisitAuthorizationJpaEntity v
			WHERE v.visitor.id = :visitorId
			  AND v.property.id = :propertyId
			  AND v.isActive = true
			  AND v.authorizationStartDate < :endDate
			  AND v.authorizationEndDate > :startDate
			""")
	boolean existsOverlappingAuthorization(@Param("visitorId") UUID visitorId, @Param("propertyId") UUID propertyId,
			@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}