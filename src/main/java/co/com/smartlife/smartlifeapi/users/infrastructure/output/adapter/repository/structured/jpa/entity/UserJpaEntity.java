package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user",  schema = "users")
public class UserJpaEntity {
	
	@Id
	@Column(name = "id", updatable = false)
	private UUID id;
	
	@Column(name = "document_number")
    private String documentNumber;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "middle_name")
    private String middleName;
	
	@Column(name = "last_name")
    private String lastName;
	
	@Column(name = "second_last_name")
    private String secondLastName;
	
	@Column(name = "email")
    private String 	email;
	
	@Column(name = "is_email_confirmed")
    private Boolean emailConfirmed;
	
	@Column(name = "is_email_confirmed_default_value")
    private Boolean emailConfirmedDefaultValue;
	
	@Column(name = "is_active")
    private Boolean isActive;
	
	@Column(name = "external_id")
	private String externalId;
	
	@ManyToOne
	@JoinColumn(name = "id_type")
    private IdTypeJpaEntity idType;
	
	@ManyToOne
	@JoinColumn(name = "id_urbanization")
    private UrbanizationUserJpaEntity urbanization;
	
	protected UserJpaEntity() {}
	
	public UserJpaEntity(final UUID id,
            final String firstName,
            final String middleName,
            final String lastName,
            final String secondLastName,
            final String documentNumber,
            final String email,
            final Boolean emailConfirmed,
            final Boolean emailConfirmedDefaultValue,
            final Boolean isActive,
            final String externalId,
            final IdTypeJpaEntity idType,
            final UrbanizationUserJpaEntity urbanization) {
		setId(id);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setSecondLastName(secondLastName);
        setDocumentNumber(documentNumber);
        setEmail(email);
        setEmailConfirmed(emailConfirmed);
        setIsActive(isActive);
        setExternalId(externalId);
        setIdType(idType);
        setUrbanization(urbanization);
        setEmailConfirmedDefaultValue(emailConfirmedDefaultValue);
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	public void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(Boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}

	public Boolean getEmailConfirmedDefaultValue() {
		return emailConfirmedDefaultValue;
	}

	public void setEmailConfirmedDefaultValue(Boolean emailConfirmedDefaultValue) {
		this.emailConfirmedDefaultValue = emailConfirmedDefaultValue;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public IdTypeJpaEntity getIdType() {
		return idType;
	}

	public void setIdType(IdTypeJpaEntity idType) {
		this.idType = idType;
	}

	public UrbanizationUserJpaEntity getUrbanization() {
		return urbanization;
	}

	public void setUrbanization(UrbanizationUserJpaEntity urbanization) {
		this.urbanization = urbanization;
	}
    
}
