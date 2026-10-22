package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.repository.structured.jpa.entity;

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
@Table(name = "user")
public class UserJpaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "middle_name")
    private String middleName;
	
	@Column(name = "lastname")
    private String lastName;
	
	@Column(name = "second_lastname")
    private String secondLastName;
	
	@Column(name = "document_number")
    private String documentNumber;
	
	@Column(name = "email")
    private String 	email;
	
	@Column(name = "is_email_confirmed")
    private Boolean emailConfirmed;
	
	@Column(name = "is_email_confirmed_default_value")
    private Boolean emailConfirmedDefaultValue;
	
	@Column(name = "is_active")
    private Boolean active;
	
	@ManyToOne
	@JoinColumn(name = "id_type", referencedColumnName = "id")
    private IdTypeJpaEntity idType;
	
	@ManyToOne
	@JoinColumn(name = "id_urbanization", referencedColumnName = "id")
    private UrbanizationUserJpaEntity urbanization;
	
	public UserJpaEntity(final UUID id,
            final String firstName,
            final String middleName,
            final String lastName,
            final String secondLastName,
            final String documentNumber,
            final String email,
            final Boolean emailConfirmed,
            final Boolean emailConfirmedDefaultValue,
            final Boolean active,
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
        setActive(active);
        setIdType(idType);
        setUrbanization(urbanization);
        setEmailConfirmedDefaultValue(emailConfirmedDefaultValue);
    }

    public UUID getId() {
        return id;
    }

    private void setId(final UUID id) {
        this.id = id;
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

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
