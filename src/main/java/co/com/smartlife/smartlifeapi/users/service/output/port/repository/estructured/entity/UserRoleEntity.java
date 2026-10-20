package co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity;

import java.util.UUID;

public class UserRoleEntity {
	
	private UUID id;
	private UserEntity user;
	private RoleEntity role;
	
	public UserRoleEntity(final UUID id, final UserEntity user, final RoleEntity role) {
		this.id = id;
		this.user = user;
		this.role = role;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

}
