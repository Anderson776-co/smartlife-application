package co.com.smartlife.smartlifeapi.users.service.output.port.repository;

import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.DeleteRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserRoleEntity;

public interface UserRoleRepository extends CreateRepository<UserRoleEntity>, ReadRepository<UUID, UserRoleEntity> ,DeleteRepository<UUID, UserRoleEntity>{
	Optional<UserRoleEntity> findByUserId(UUID userId);
	
	Optional<UserRoleEntity> findByRoleId(UUID roleId);

}
