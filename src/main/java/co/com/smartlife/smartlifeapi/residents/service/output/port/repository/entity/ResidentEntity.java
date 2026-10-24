package co.com.smartlife.smartlifeapi.residents.service.output.port.repository.entity;

import java.util.UUID;

public class ResidentEntity {
	private UUID id;
	private UserResidentEntity user;
	
	public ResidentEntity(final UUID id, final UserResidentEntity user) {
		setId(id);
		setUser(user);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserResidentEntity getUser() {
		return user;
	}

	public void setUser(UserResidentEntity user) {
		this.user = user;
	}
}
