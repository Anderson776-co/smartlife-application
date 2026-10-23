package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity.ResidentResidenceJpaEntity;

public interface ResidentResidenceJpaRepository extends JpaRepository<ResidentResidenceJpaEntity, UUID>{
	
}
