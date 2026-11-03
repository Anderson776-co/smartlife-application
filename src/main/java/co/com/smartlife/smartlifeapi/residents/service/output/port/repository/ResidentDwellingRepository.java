package co.com.smartlife.smartlifeapi.residents.service.output.port.repository;

import java.util.UUID;

import co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity.ResidentResidenceEntity;
import co.com.smartlife.smartlifeapi.shared.repository.CreateRepository;
import co.com.smartlife.smartlifeapi.shared.repository.DeleteRepository;
import co.com.smartlife.smartlifeapi.shared.repository.ReadRepository;
import co.com.smartlife.smartlifeapi.shared.repository.UpdateRepository;

public interface ResidentDwellingRepository
extends CreateRepository<ResidentResidenceEntity>,
        ReadRepository<UUID, ResidentResidenceEntity>,
        UpdateRepository<UUID, ResidentResidenceEntity>,
        DeleteRepository<UUID, ResidentResidenceEntity> {
}
