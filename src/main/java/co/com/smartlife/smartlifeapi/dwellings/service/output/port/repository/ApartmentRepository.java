package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.ApartmentEntity;
import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;

public interface ApartmentRepository extends CreateRepository<ApartmentEntity>, ReadRepository<UUID, ApartmentEntity>,
UpdateRepository<UUID, ApartmentEntity>{

}
