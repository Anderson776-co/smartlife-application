package co.com.smartlife.smartlifeapi.users.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.service.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;

public interface IdTypeRepository extends ReadRepository<UUID, IdTypeEntity>{

}
