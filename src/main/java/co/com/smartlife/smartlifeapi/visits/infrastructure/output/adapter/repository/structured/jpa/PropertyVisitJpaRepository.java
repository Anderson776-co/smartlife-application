package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity.PropertyVisitJpaEntity;

public interface PropertyVisitJpaRepository extends JpaRepository<PropertyVisitJpaEntity, UUID>,
JpaSpecificationExecutor<PropertyVisitJpaEntity> {
	

}
