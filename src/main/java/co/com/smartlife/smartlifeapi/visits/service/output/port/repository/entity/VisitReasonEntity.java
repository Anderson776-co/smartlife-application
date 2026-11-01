package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class VisitReasonEntity {
	
	private UUID id;
	private String name;
	private Boolean requiresDocument;
	
	public VisitReasonEntity(final UUID id, final String name, final Boolean requiresDocument) {
		setId(id);
		setName(name);
		setRequiresDocument(requiresDocument);
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(final UUID id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	public Boolean getRequiresDocument() {
		return requiresDocument;
	}

	public void setRequiresDocument(Boolean requiresDocument) {
		this.requiresDocument = requiresDocument;
	}
}
