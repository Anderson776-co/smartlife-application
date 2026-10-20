package co.com.smartlife.smartlifeapi.visits.service.output.port.repository;

import java.util.Optional;
import java.util.UUID;

import co.com.smartlife.smartlifeapi.shared.services.output.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.services.output.repository.UpdateRepository;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

public interface VisitorRepository extends CreateRepository<VisitorEntity>, ReadRepository<UUID, VisitorEntity>, 
UpdateRepository<UUID, VisitorEntity>{
	
	Optional<VisitorEntity> findByEmail(final String email);
	
	Optional<VisitorEntity> findByDocumentNumber(final String documentNumber);

}
