package co.com.smartlife.smartlifeapi.users.service.output.repository.entity;

import java.util.UUID;

public class ResidentEntity {
	private UUID id;
	private UserEntity user;
	private UUID dwellingId;
	
	public ResidentEntity(final UUID id, final UserEntity user, final UUID dwellingId) {
		setId(id);
		setUser(user);
		setDwellingId(dwellingId);
	}

	public UUID getId() {
		return id;
	}

	private void setId(UUID id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	private void setUser(UserEntity user) {
		this.user = user;
	}

	public UUID getDwellingId() {
		return dwellingId;
	}

	private void setDwellingId(UUID dwellingId) {
		this.dwellingId = dwellingId;
	}
	
	
}
