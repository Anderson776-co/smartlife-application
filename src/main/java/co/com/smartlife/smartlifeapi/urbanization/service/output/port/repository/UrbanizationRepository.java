package co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.urbanization.service.output.port.repository.entity.UrbanizationEntity;

public interface UrbanizationRepository extends CreateRepository<UrbanizationEntity>, ReadRepository<UUID, UrbanizationEntity>{

}
