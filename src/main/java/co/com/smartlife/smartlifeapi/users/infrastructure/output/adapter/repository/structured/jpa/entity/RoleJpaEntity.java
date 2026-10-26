package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role", schema = "users")
public class RoleJpaEntity {
	@Id
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "name")
	private String name;
	
	protected RoleJpaEntity() {}
	
	public RoleJpaEntity(final UUID id, final String name) {
        setId(id);
        setName(name);
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
