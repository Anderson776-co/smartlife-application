package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationDwellingJpaEntity;

public interface UrbanizationDwellingJpaRepository extends JpaRepository<UrbanizationDwellingJpaEntity, UUID>{

}
