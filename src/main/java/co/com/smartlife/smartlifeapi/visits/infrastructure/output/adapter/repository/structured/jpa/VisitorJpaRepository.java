package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitorEntityJpa;

public interface VisitorJpaRepository extends JpaRepository<VisitorEntityJpa, UUID>{
	
	Optional<VisitorEntityJpa> findByEmail(final String email);
	
	Optional <VisitorEntityJpa> findByDocumentNumber(final String documentNumber);

}
