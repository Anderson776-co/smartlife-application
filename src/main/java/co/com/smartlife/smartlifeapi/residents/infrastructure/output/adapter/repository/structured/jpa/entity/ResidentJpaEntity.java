package co.com.smartlife.smartlifeapi.residents.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "resident", schema = "residents")
public class ResidentJpaEntity {
	
	@Id
	private UUID id;
	
	@OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
	private UserResidentJpaEntity user;
	
	public ResidentJpaEntity(final UUID id, final UserResidentJpaEntity user) {
		setId(id);
		setUser(user);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserResidentJpaEntity getUser() {
		return user;
	}

	public void setUser(UserResidentJpaEntity user) {
		this.user = user;
	}
}
