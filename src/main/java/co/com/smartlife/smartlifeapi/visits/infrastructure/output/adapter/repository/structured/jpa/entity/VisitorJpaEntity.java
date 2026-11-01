package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.repository.structured.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visitor", schema = "visits")
public class VisitorJpaEntity {

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
	private String email;
	
	@Column(name = "vehicle_plate")
	private String vehiclePlate;
	
	protected VisitorJpaEntity() {}

	public VisitorJpaEntity(final UUID id, final String documentNumber, final String firstName, final String middleName, 
			final String lastName, final String secondLastName, final String email, final String vehiclePlate) {
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

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
}
