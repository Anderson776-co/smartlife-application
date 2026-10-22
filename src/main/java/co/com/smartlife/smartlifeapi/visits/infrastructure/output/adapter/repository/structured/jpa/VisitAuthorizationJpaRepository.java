package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.VisitAuthorizationEntityJpa;

public interface VisitAuthorizationJpaRepository extends JpaRepository<VisitAuthorizationEntityJpa, UUID>{

}
