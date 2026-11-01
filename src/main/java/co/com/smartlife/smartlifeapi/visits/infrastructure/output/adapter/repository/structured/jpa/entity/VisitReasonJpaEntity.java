package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_reason", schema = "visits")
public class VisitReasonJpaEntity {
	
	@Id
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "requires_document")
	private Boolean requiresDocument;

	protected VisitReasonJpaEntity() {}
	
	public VisitReasonJpaEntity(final UUID id, final String name, final Boolean requiresDocument) {
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
