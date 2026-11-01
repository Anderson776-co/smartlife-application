package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitReasonJpaEntity;

public interface VisitReasonJpaRepository extends JpaRepository<VisitReasonJpaEntity, UUID>{
	

}
