package co.com.smartlife.smartlifeapi.users.service.output.port.repository.estructured.entity;

import java.util.UUID;

public class RoleEntity {
	
	private UUID id;
    private String name;

    public RoleEntity(final UUID id, final String name) {
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

    private void setName(final String name) {
        this.name = name;
    }

}
