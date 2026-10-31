package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitResidentJpaEntity;

public interface VisitResidentJpaRepository extends JpaRepository<VisitResidentJpaEntity, UUID>{
	
	boolean existsByIdAndPropertyId(UUID id, UUID propertyId);

}
