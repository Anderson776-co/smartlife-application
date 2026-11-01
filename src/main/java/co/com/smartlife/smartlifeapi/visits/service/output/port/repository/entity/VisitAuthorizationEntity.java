package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.time.Instant;
import java.util.UUID;

public class VisitAuthorizationEntity {
	private UUID id;
	private Instant authorizationStartDate;
	private Instant authorizationEndDate;
	private Instant registerDate;
	private String observation;
	private String qrCode;
	private String fileReference;
	private Boolean isActive;
	private PropertyVisitEntity property;
	private AuthorizatorEntity authorizator;
	private VisitorEntity visitor;
	private VisitReasonEntity visitReason;
	
	public VisitAuthorizationEntity(final UUID id, final Instant authorizationStartDate, final Instant authorizationEndDate,
			final Instant registerDate, final String observation, final String qrCode, final String fileReference, final Boolean isActive,
			final PropertyVisitEntity property, final AuthorizatorEntity authorizator, final VisitorEntity visitor, final VisitReasonEntity visitReason) {
		setId(id);
		setAuthorizationStartDate(authorizationStartDate);
		setAuthorizationEndDate(authorizationEndDate);
		setRegisterDate(registerDate);
		setObservation(observation);
		setQrCode(qrCode);
		setFileReference(fileReference);
		setIsActive(isActive);
		setProperty(property);
		setAuthorizator(authorizator);
		setVisitReason(visitReason);
		setVisitor(visitor);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Instant getAuthorizationStartDate() {
		return authorizationStartDate;
	}

	public void setAuthorizationStartDate(Instant authorizationStartDate) {
		this.authorizationStartDate = authorizationStartDate;
	}

	public Instant getAuthorizationEndDate() {
		return authorizationEndDate;
	}

	public void setAuthorizationEndDate(Instant authorizationEndDate) {
		this.authorizationEndDate = authorizationEndDate;
	}

	public Instant getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Instant registerDate) {
		this.registerDate = registerDate;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getFileReference() {
		return fileReference;
	}

	public void setFileReference(String fileReference) {
		this.fileReference = fileReference;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public PropertyVisitEntity getProperty() {
		return property;
	}

	public void setProperty(PropertyVisitEntity property) {
		this.property = property;
	}

	public AuthorizatorEntity getAuthorizator() {
		return authorizator;
	}

	public void setAuthorizator(AuthorizatorEntity authorizator) {
		this.authorizator = authorizator;
	}

	public VisitorEntity getVisitor() {
		return visitor;
	}

	public void setVisitor(VisitorEntity visitor) {
		this.visitor = visitor;
	}

	public VisitReasonEntity getVisitReason() {
		return visitReason;
	}

	public void setVisitReason(VisitReasonEntity visitReason) {
		this.visitReason = visitReason;
	}
	
}
