package co.com.smartlife.smartlifeapi.users.service.input.dto.response;

import java.time.Duration;

public record SessionDto(String cookieValue, Duration expiresIn) {

}
