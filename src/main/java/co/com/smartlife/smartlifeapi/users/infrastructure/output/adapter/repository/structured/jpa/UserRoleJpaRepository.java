package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.UserRoleJpaEntity;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UUID> {
	
	Optional<UserRoleJpaEntity> findByUserId(final UUID userId);
	
	Optional<UserRoleJpaEntity> findByRoleId(final UUID roleId);

}
