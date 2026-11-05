package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitResidentJpaEntity;

public interface VisitResidentJpaRepository extends JpaRepository<VisitResidentJpaEntity, UUID>, JpaSpecificationExecutor<VisitResidentJpaEntity> {
	
	boolean existsByIdAndPropertyId(UUID id, UUID propertyId);

}
