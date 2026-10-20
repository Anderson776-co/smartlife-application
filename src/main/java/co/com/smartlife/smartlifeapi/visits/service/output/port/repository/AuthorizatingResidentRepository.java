package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.services.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizingResidentEntity;

public interface AuthorizatingResidentRepository extends ReadRepository<UUID, AuthorizingResidentEntity>{

}
