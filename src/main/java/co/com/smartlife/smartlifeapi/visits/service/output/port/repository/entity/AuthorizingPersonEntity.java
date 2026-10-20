package co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity;

import java.util.UUID;

public interface AuthorizingPersonEntity {
	
	UUID getId();

    String getDocumentNumber();

    String getFullName();

    boolean getActive();

}
