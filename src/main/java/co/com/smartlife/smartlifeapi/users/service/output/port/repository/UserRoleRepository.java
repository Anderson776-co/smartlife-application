package co.com.smartlife.smartlifeapi.users.service.output.port.repository;

import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.services.output.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.DeleteRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

public interface UserRoleRepository extends CreateRepository<UserRoleEntity>, ReadRepository<UUID, UserRoleEntity> ,DeleteRepository<UUID, UserRoleEntity>{
	Optional<UserRoleEntity> findByUserId(UUID userId);
	
	Optional<UserRoleEntity> findByRoleId(UUID roleId);

}
