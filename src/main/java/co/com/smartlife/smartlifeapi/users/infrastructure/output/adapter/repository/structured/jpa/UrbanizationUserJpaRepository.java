package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity.UrbanizationUserJpaEntity;

public interface UrbanizationUserJpaRepository extends JpaRepository<UrbanizationUserJpaEntity, UUID> {

}
