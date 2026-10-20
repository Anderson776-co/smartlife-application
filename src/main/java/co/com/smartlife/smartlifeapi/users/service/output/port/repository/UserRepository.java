package co.com.smartlife.smartlifeapi.users.service.output.port.repository;

import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.services.output.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity.UserEntity;

public interface UserRepository extends CreateRepository<UserEntity>, ReadRepository<UUID, UserEntity>, UpdateRepository<UUID, UserEntity>{
	
	Optional<UserEntity> findByEmail(final String email);
	
	Optional<UserEntity> findByDocumentNumber(final String documentNumber);

}
