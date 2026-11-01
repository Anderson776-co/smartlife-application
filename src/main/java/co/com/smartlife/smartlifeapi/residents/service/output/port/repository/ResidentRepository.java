package co.com.smartlife.smartlifeapi.residents.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentEntity;
import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;

public interface ResidentRepository extends CreateRepository<ResidentEntity>, ReadRepository<UUID, ResidentEntity>{

}
