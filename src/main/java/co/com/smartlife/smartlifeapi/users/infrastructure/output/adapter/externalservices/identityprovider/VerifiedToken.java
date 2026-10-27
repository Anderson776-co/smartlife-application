package co.com.smartlife.smartlifeapi.users.infrastructure.output.adapter.externalservices.identityprovider;

public record VerifiedToken(String uid, String email, boolean emailVerified, Long authTime) {

}
