package co.com.smartlife.smartlifeapi.users.infrastructure.output.repository.estructured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRoleJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "id_user", referencedColumnName = "id")
	private UserJpaEntity user;
	
	@ManyToOne
	@JoinColumn(name = "id_role", referencedColumnName = "id")
	private RoleJpaEntity role;
	
	public UserRoleJpaEntity(final UUID id, final UserJpaEntity user, final RoleJpaEntity role) {
        setId(id);
        setUser(user);
        setRole(role);
    }

    public UUID getId() {
        return id;
    }

    private void setId(final UUID id) {
        this.id = id;
    }

	public UserJpaEntity getUser() {
		return user;
	}

	public void setUser(UserJpaEntity user) {
		this.user = user;
	}

	public RoleJpaEntity getRole() {
		return role;
	}

	public void setRole(RoleJpaEntity role) {
		this.role = role;
	}
    
    
}
