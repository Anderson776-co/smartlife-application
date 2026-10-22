package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity.IdTypeJpaEntity;

public interface IdTypeJpaRepository extends JpaRepository<IdTypeJpaEntity, UUID> {

}
