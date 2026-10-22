package co.com.smartlife.smartlifeapi.users.service.usecase.contract;

public interface AuthenticateUserUseCase {
	
	AuthenticatedSessionDTO authenticate(String idToken);

    record AuthenticatedSessionDTO(
            AuthenticatedSessionDTO user,
            String sessionCookie) {
    }
}
