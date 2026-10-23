 package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public class VisitorEntity {
	private UUID id;
	private String documentNumber;
	private String firstName;
	private String middleName;
	private String lastName;
	private String secondLastName;
	private String email;
	private String vehiclePlate;

	public VisitorEntity(
		final UUID id,
		final String firstName,
		final String middleName,
		final String lastName,
		final String secondLastName,
		final String documentNumber,
		final String email,
		final String vehiclePlate) {
		
		setId(id);
		setFirstName(firstName);
		setMiddleName(middleName);
		setLastName(lastName);
		setSecondLastName(secondLastName);
		setDocumentNumber(documentNumber);
		setEmail(email);
		setVehiclePlate(vehiclePlate);
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

	private void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	private void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	private void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getSecondLastName() {
		return secondLastName;
	}

	private void setSecondLastName(final String secondLastName) {
		this.secondLastName = secondLastName;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	private void setDocumentNumber(final String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
	public String getEmail() {
		return email;
	}
	
	private void setEmail(final String email) {
		this.email = email;
	}
	
	public String getVehiclePlate() {
		return vehiclePlate;
	}
	
	private void setVehiclePlate(final String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
}
