package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "resident", schema = "visits")
public class VisitResidentJpaEntity {
	
	@Id
	@Column(name = "id")
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "id_property")
	private PropertyVisitJpaEntity property;
	
	protected VisitResidentJpaEntity() {}
	
	public VisitResidentJpaEntity(final UUID id, final PropertyVisitJpaEntity property) {
		this.id = id;
		this.property = property;
	}

	public UUID getId() {
		return id;
	}

	public PropertyVisitJpaEntity getProperty() {
		return property;
	}
}
