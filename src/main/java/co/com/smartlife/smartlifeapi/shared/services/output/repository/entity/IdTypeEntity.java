package co.com.smartlife.smartlifeapi.shared.services.output.repository.entity;

import java.util.UUID;

public class IdTypeEntity {
	
	private UUID id;
    private String name;

    public IdTypeEntity(final UUID id, final String name) {
        setId(id);
        setName(name);
    }
    
    public IdTypeEntity() {
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

    private void setName(final String name) {
        this.name = name;
    }
}
