package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "urbanization")
public class UrbanizationUserJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@Column(name = "name")
	private String name;
	
	public UrbanizationUserJpaEntity(final UUID id, final String name) {
        super();
        setId(id);
        setName(name);
    }
    
    public UrbanizationUserJpaEntity() {
        super();
    }

    public UUID getId() {
        return id;
    }

    private void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
