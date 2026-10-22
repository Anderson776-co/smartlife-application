package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.time.Instant;
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
@Table(name = "visit_authorization")
public class VisitAuthorizationEntityJpa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@Column(name = "start_date")
	private Instant authorizationStartDate;
	
	@Column(name = "end_date")
	private Instant authorizationEndDate;
	
	@Column(name = "register_date")
	private Instant registerDate;
	
	@Column(name = "observation")
	private String observation;
	
	@ManyToOne
	@JoinColumn(name = "id_visit_reason", referencedColumnName = "id")
	private VisitReasonEntityJpa visitReason;
	
	@ManyToOne
	@JoinColumn(name = "id_property", referencedColumnName = "id")
	private PropertyVisitEntityJpa property;
	
	@ManyToOne
	@JoinColumn(name = "id_visitor", referencedColumnName = "id")
	private VisitorEntityJpa visitor;
	
	@Column(name = "id_authorizer", nullable = false, updatable = false)
	private UUID authorizerId;
	
	
	
	public VisitAuthorizationEntityJpa() {
	}

	public VisitAuthorizationEntityJpa(final UUID id, final Instant authorizationStartDate, final Instant authorizationEndDate,
			final Instant registerDate, final String observation, final VisitReasonEntityJpa visitReason, final PropertyVisitEntityJpa property,
			VisitorEntityJpa visitor, final UUID authorizerId) {
		setId(id);
		setAuthorizationStartDate(authorizationStartDate);
		setAuthorizationEndDate(authorizationEndDate);
		setRegisterDate(registerDate);
		setObservation(observation);
		setVisitReason(visitReason);
		setPropertyVisit(property);
		setVisitor(visitor);
		setAuthorizerId(authorizerId);
	}
	
	public UUID getId() {
		return id;
	}
	
	private void setId(final UUID id) {
		this.id = id;
	}
	
	public Instant getStartDate() {
		return authorizationStartDate;
	}
	
	private void setAuthorizationStartDate(final Instant authorizationStartDate) {
		this.authorizationStartDate = authorizationStartDate;
	}
	
	public Instant getExpirationDate() {
		return authorizationEndDate;
	}
	
	private void setAuthorizationEndDate(final Instant authorizationEndDate) {
		this.authorizationEndDate = authorizationEndDate;
	}
	
	public Instant getRegisterDate() {
		return registerDate;
	}
	
	private void setRegisterDate(final Instant registerDate) {
		this.registerDate = registerDate;
	}
	
	public String getObservation() {
		return observation;
	}
	
	private void setObservation(final String observation) {
		this.observation = observation;
	}
	
	public VisitReasonEntityJpa getReasonVisit() {
		return visitReason;
	}
	
	private void setVisitReason(final VisitReasonEntityJpa visitReason) {
		this.visitReason = visitReason;
	}
	
	public PropertyVisitEntityJpa getPropertyVisit() {
		return property;
	}
	
	private void setPropertyVisit(final PropertyVisitEntityJpa property) {
		this.property = property;
	}

	public VisitorEntityJpa getVisitor() {
		return visitor;
	}
	
	private void setVisitor(final VisitorEntityJpa visitor) {
		this.visitor = visitor;
	}

	public UUID getAuthorizerId() {
		return authorizerId;
	}

	private void setAuthorizerId(UUID authorizerId) {
		this.authorizerId = authorizerId;
	}
}
