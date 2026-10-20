package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.time.Instant;
import java.util.UUID;

public class VisitAuthorizationEntity {
	private UUID id;
	private Instant authorizationStartDate;
	private Instant authorizationEndDate;
	private Instant registerDate;
	private String observation;
	private VisitReasonEntity visitReason;
	private PropertyVisitEntity property;
	private VisitorEntity visitor;
	
	public VisitAuthorizationEntity(final UUID id, final Instant authorizationStartDate, final Instant authorizationEndDate,
			final Instant registerDate, final String observation, final VisitReasonEntity visitReason, final PropertyVisitEntity property) {
		setId(id);
		setAuthorizationStartDate(authorizationStartDate);
		setAuthorizationEndDate(authorizationEndDate);
		setRegisterDate(registerDate);
		setObservation(observation);
		setVisitReason(visitReason);
		setPropertyVisit(property);
		setVisitor(visitor);
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
	
	public VisitReasonEntity getReasonVisit() {
		return visitReason;
	}
	
	private void setVisitReason(final VisitReasonEntity visitReason) {
		this.visitReason = visitReason;
	}
	
	public PropertyVisitEntity getPropertyVisit() {
		return property;
	}
	
	private void setPropertyVisit(final PropertyVisitEntity property) {
		this.property = property;
	}

	public VisitorEntity getVisitor() {
		return visitor;
	}
	
	private void setVisitor(final VisitorEntity visitor) {
		this.visitor = visitor;
	}
	
}
