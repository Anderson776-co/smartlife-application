package co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.repository.estructured.jpa.entity.AuthorizingResidentEntityJpa;

public interface AuthorizingResidentJpaRepository extends JpaRepository<AuthorizingResidentEntityJpa, UUID>{
	
	 Optional<AuthorizingResidentEntityJpa> findById(UUID id);

}
