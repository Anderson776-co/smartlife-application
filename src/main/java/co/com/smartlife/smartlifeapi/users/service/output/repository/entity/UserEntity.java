package co.com.smartlife.smartlifeapi.users.service.output.repository.entity;

import java.util.UUID;

public class UserEntity {
	private UUID id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String secondLastName;
	private String documentNumber;
	private String email;
	private boolean isActive;
	
	
	
	public UserEntity(UUID id, String firstName, String middleName, String lastName, String secondLastName,
			String documentNumber, String email, boolean isActive) {
		
		setId(id);
		setFirstName(firstName);
		setMiddleName(middleName);
		setLastName(lastName);
		setSecondLastName(secondLastName);
		setDocumentNumber(documentNumber);
		setEmail(email);
		setIsActive(isActive);
	}

	public UUID getId() {
		return id;
	}

	private void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	private void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	private void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	private void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	private void setSecondLastName(String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	private void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getEmail() {
		return email;
	}

	private void setEmail(String email) {
		this.email = email;
	}

	public boolean getIsActive() {
		return isActive;
	}

	private void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
