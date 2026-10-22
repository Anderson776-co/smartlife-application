package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.service.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingOwnerEntity;

public interface AuthorizatingOwnerRepository extends ReadRepository<UUID, AuthorizingOwnerEntity>{

}
