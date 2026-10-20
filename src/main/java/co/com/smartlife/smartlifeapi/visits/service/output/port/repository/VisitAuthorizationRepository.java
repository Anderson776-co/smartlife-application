package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.services.output.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;

public interface VisitAuthorizationRepository extends CreateRepository<VisitAuthorizationEntity>, ReadRepository<UUID, VisitAuthorizationEntity>, 
UpdateRepository<UUID, VisitAuthorizationEntity>{

}
