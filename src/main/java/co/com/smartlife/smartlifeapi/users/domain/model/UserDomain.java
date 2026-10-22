package co.com.smartlife.smartlifeapi.users.domain.model;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.IdTypeEntity;
import co.com.smartlife.smartlifeapi.users.service.output.port.repository.entity.UrbanizationUserEntity;

@Component
public class UserDomain {
	private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String documentNumber;
    private String 	email;
    private Boolean emailConfirmed;
    private Boolean emailConfirmedDefaultValue;
    private Boolean active;
    private IdTypeEntity idType;
    private UrbanizationUserEntity urbanization;
    
	public UserDomain(UUID id, String firstName, String middleName, String lastName, String secondLastName,
			String documentNumber, String email, Boolean emailConfirmed, Boolean emailConfirmedDefaultValue,
			Boolean active, IdTypeEntity idType, UrbanizationUserEntity urbanization) {
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.secondLastName = secondLastName;
		this.documentNumber = documentNumber;
		this.email = email;
		this.emailConfirmed = emailConfirmed;
		this.emailConfirmedDefaultValue = emailConfirmedDefaultValue;
		this.active = active;
		this.idType = idType;
		this.urbanization = urbanization;
	}
    
    
}
