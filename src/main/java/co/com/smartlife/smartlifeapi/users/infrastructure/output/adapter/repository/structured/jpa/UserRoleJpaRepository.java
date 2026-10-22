package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserRoleJpaEntity;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UUID> {
	
	Optional<UserRoleJpaEntity> findByUserId(final UUID userId);
	
	Optional<UserRoleJpaEntity> findByRoleId(final UUID roleId);

}
