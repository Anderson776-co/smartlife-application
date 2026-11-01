package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "visit_authorization", schema = "visits")
public class VisitAuthorizationJpaEntity {
	
	@Id
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "start_date")
	private Instant authorizationStartDate;
	
	@Column(name = "end_date")
	private Instant authorizationEndDate;
	
	@Column(name = "register_date")
	private Instant registerDate;
	
	@Column(name = "observation")
	private String observation;
	
	@Column(name = "qr_code")
	private String qrCode;
	
	@Column(name = "file_reference")
	private String fileReference;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@ManyToOne
	@JoinColumn(name = "id_property")
	private PropertyVisitJpaEntity property;
	
	@ManyToOne
	@JoinColumn(name = "id_authorizator")
	private AuthorizatorJpaEntity authorizator;
	
	@ManyToOne
	@JoinColumn(name = "id_visitor")
	private VisitorJpaEntity visitor;
	
	@ManyToOne
	@JoinColumn(name = "id_visit_reason")
	private VisitReasonJpaEntity visitReason;
	
	protected VisitAuthorizationJpaEntity() {}

	public VisitAuthorizationJpaEntity(final UUID id, final Instant authorizationStartDate, final Instant authorizationEndDate,
			final Instant registerDate, final String observation, final String qrCode, final String fileReference, final Boolean isActive, 
			final PropertyVisitJpaEntity property, final AuthorizatorJpaEntity authorizator, final VisitorJpaEntity visitor, final VisitReasonJpaEntity visitReason) {
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

	public PropertyVisitJpaEntity getProperty() {
		return property;
	}

	public void setProperty(PropertyVisitJpaEntity property) {
		this.property = property;
	}

	public AuthorizatorJpaEntity getAuthorizator() {
		return authorizator;
	}

	public void setAuthorizator(AuthorizatorJpaEntity authorizator) {
		this.authorizator = authorizator;
	}

	public VisitorJpaEntity getVisitor() {
		return visitor;
	}

	public void setVisitor(VisitorJpaEntity visitor) {
		this.visitor = visitor;
	}

	public VisitReasonJpaEntity getVisitReason() {
		return visitReason;
	}

	public void setVisitReason(VisitReasonJpaEntity visitReason) {
		this.visitReason = visitReason;
	}
}
