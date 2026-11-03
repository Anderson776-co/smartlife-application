package co.com.smartlife.smartlifeapi.users.domain;

import java.util.UUID;

public record UserStatusChangeDomain(UUID userId, Boolean active) {
}
