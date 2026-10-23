package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.VisitorJpaEntity;

public interface VisitorJpaRepository extends JpaRepository<VisitorJpaEntity, UUID>{
	
	Optional<VisitorJpaEntity> findByEmail(final String email);
	
	Optional <VisitorJpaEntity> findByDocumentNumber(final String documentNumber);

}
