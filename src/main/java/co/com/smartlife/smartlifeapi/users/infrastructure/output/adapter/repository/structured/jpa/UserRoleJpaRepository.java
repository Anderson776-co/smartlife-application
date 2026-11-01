package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UserRoleJpaEntity;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpaEntity, UUID> {
	
	List<UserRoleJpaEntity> findByUserId(final UUID userId);
	
	List<UserRoleJpaEntity> findByRoleId(final UUID roleId);

}
