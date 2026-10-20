package co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity;

import java.util.UUID;

public class UserEntity {
	private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String documentNumber;
    private String 	email;
    private Boolean isEmailConfirmed;
    private Boolean isEmailConfirmedDefaultValue;
    private Boolean isActive;
    private IdTypeEntity idType;
    private UrbanizationUserEntity urbanization;

    public UserEntity(
            final UUID id,
            final String firstName,
            final String middleName,
            final String lastName,
            final String secondLastName,
            final String documentNumber,
            final String email,
            final Boolean isEmailConfirmed,
            final Boolean isEmailConfirmedDefaultValue,
            final Boolean isActive,
            final IdTypeEntity idType,
            final UrbanizationUserEntity urbanization) {

        setId(id);
        setFirstName(firstName);
        setMiddleName(middleName);
        setLastName(lastName);
        setSecondLastName(secondLastName);
        setDocumentNumber(documentNumber);
        setEmail(email);
        setIsEmailConfirmed(isEmailConfirmed);
        setIsActive(isActive);
        setIdType(idType);
        setUrbanization(urbanization);
        setIsEmailConfirmedDefaultValue(isEmailConfirmedDefaultValue);
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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

	public Boolean getIsEmailConfirmed() {
		return isEmailConfirmed;
	}

	public void setIsEmailConfirmed(Boolean isEmailConfirmed) {
		this.isEmailConfirmed = isEmailConfirmed;
	}

	public Boolean getIsEmailConfirmedDefaultValue() {
		return isEmailConfirmedDefaultValue;
	}

	public void setIsEmailConfirmedDefaultValue(Boolean isEmailConfirmedDefaultValue) {
		this.isEmailConfirmedDefaultValue = isEmailConfirmedDefaultValue;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public IdTypeEntity getIdType() {
		return idType;
	}

	public void setIdType(IdTypeEntity idType) {
		this.idType = idType;
	}

	public UrbanizationUserEntity getUrbanization() {
		return urbanization;
	}
    
	public void setUrbanization(UrbanizationUserEntity urbanization) {
		this.urbanization = urbanization;
	}

}
