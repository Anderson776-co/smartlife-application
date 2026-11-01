package co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.dwellings.service.output.port.repository.entity.UserOwnerEntity;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;

public interface UserOwnerRepository extends ReadRepository<UUID, UserOwnerEntity>{

}
