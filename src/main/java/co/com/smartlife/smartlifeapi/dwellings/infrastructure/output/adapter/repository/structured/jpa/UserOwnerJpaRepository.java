package co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.smartlife.smartlifeapi.dwellings.infrastructure.output.adapter.repository.structured.jpa.entity.UserOwnerJpaEntity;

public interface UserOwnerJpaRepository extends JpaRepository<UserOwnerJpaEntity, UUID>{

}
