package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidenceJpaEntity;

public interface ResidenceJpaRepository extends JpaRepository<ResidenceJpaEntity, UUID> {

}
