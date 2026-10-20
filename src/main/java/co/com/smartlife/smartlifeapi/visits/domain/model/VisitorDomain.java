package co.com.smartlife.smartlifeapi.visits.domain.model;

import java.util.UUID;

public class VisitorDomain {
	private UUID id;
	private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String documentNumber;
    private String email;
    private String vehiclePlate;
    
    private VisitorDomain(
    		final String firstName,
    		final String middleName,
    		final String lastName,
    		final String secondLastName,
    		final String documentNumber,
    		final String email,
    		final String vehiclePlate) {
    	
    	super();
    	
    	generateId();
    	
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
    
    private void generateId() {
        this.id = UUID.randomUUID();
    }
    
    public void regenerateId() {
        generateId();
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getEmail() {
        return email;
    }
    
    public String getVehiclePlate() {
    	return vehiclePlate;
    }
    
    private void setFirstName(final String firstName) {
    	this.firstName = firstName;
    }
    
    private void setMiddleName(final String middleName) {
    	this.middleName = middleName;
    }
    
    private void setLastName(final String lastName) {
        this.lastName = lastName;
    }
    
    private void setSecondLastName(final String secondLastName) {
        this.secondLastName = secondLastName;
    }
    
    private void setDocumentNumber(final String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    private void setEmail(final String email) {
        this.email = email;
    }
    
    private void setVehiclePlate(final String vehiclePlate) {
    	this.vehiclePlate = vehiclePlate;
    }
}
