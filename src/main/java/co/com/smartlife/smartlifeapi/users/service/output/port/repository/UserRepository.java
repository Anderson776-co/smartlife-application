package co.com.smartlife.smartlifeapi.users.service.output.port.repository;

import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UserEntity;

public interface UserRepository extends CreateRepository<UserEntity>, ReadRepository<UUID, UserEntity>, UpdateRepository<UUID, UserEntity>{
	
	Optional<UserEntity> findByEmail(final String email);
	
	Optional<UserEntity> findByDocumentNumber(final String documentNumber);
	
	Optional<UserEntity> findByIdentityProviderUid(String identityProviderUid);

}
