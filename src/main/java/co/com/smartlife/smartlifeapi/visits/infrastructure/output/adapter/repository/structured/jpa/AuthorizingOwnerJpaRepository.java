package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.AuthorizingOwnerEntityJpa;

public interface AuthorizingOwnerJpaRepository extends JpaRepository<AuthorizingOwnerEntityJpa, UUID>{
	
	 Optional<AuthorizingOwnerEntityJpa> findById(UUID id);

}
