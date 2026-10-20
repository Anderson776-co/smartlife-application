package co.com.smartlife.smartlifeapi.users.service.output.port.externalservices;

import co.com.smartlife.smartlifeapi.users.service.output.dto.AuthenticatedUserDTO;

public interface IdentityProviderOutputPort {

	AuthenticatedUserDTO verifyToken(String idToken);
}