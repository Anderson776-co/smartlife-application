package co.com.smartlife.smartlifeapi.users.service.output.repository.entity;

import java.util.UUID;

public class OwnerEntity {
	private UUID id;
	private UserEntity user;
	
	public OwnerEntity(final UUID id, final UserEntity user) {
		setId(id);
		setUser(user);
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
}
