package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.VisitReasonEntityJpa;

public interface VisitReasonJpaRepository extends JpaRepository<VisitReasonEntityJpa, UUID>{
	

}
