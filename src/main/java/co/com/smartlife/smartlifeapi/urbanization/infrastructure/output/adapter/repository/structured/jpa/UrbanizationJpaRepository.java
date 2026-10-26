package co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.urbanization.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationJpaEntity;

public interface UrbanizationJpaRepository extends JpaRepository<UrbanizationJpaEntity, UUID> {

}
