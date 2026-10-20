package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
	 Optional<UserJpaEntity> findByEmail(final String email);
	    
	 Optional<UserJpaEntity> findByDocumentNumber(final String documentNumber);

}
