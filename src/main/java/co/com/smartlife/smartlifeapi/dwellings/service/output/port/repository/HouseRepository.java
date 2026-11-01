package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.HouseEntity;
import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;

public interface HouseRepository extends CreateRepository<HouseEntity>, ReadRepository<UUID, HouseEntity>,
UpdateRepository<UUID, HouseEntity>{

}
