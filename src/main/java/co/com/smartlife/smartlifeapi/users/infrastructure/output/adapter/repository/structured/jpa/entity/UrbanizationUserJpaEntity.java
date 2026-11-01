package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "urbanization", schema = "users")
public class UrbanizationUserJpaEntity {
	@Id
	@Column(name = "id")
	private UUID id;
	
	@Column(name = "name")
	private  String name;
	
	protected UrbanizationUserJpaEntity() {}
	
    public UrbanizationUserJpaEntity(final UUID id, final String name) {
		this.id = id;
		this.name = name;
	}


	public UUID getId() {
        return id;
    }


    public String getName() {
        return name;
    }
}
