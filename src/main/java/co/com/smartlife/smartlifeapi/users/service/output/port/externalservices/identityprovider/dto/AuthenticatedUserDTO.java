package co.com.smartlife.smartlifeapi.users.service.output.port.externalservices.identityprovider.dto;

public record AuthenticatedUserDTO(String uid, String email, Boolean emailVerified) {
}