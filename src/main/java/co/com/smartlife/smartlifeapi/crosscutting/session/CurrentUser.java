package co.com.smartlife.smartlifeapi.crosscutting.session;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public record CurrentUser(UUID userId, String email, Set<String> roles) {
	public boolean hasAnyRole(final String... expected) {
        return Arrays.stream(expected).anyMatch(roles::contains);
    }

}
