package co.com.smartlife.smartlifeapi.residents.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.UserResidentEntity;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;

public interface UserResidentRepository extends ReadRepository<UUID, UserResidentEntity>{

}
