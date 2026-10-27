package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitResidentEntity;

public interface VisitResidentRepository extends ReadRepository<UUID, VisitResidentEntity>{

}
