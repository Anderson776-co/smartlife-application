package co.com.smartlife.smartlifeapi.users.service.input.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeUserStatusDtoRequest(@JsonIgnore @Schema(hidden = true) UUID userId, Boolean active) {

	public ChangeUserStatusDtoRequest {
		userId = UUIDHelper.getUUIDHelper().getDefault(userId);
	}

	/** El identificador sale de la direccion, no del cuerpo de la peticion. */
	public ChangeUserStatusDtoRequest withUserId(final UUID userId) {
		return new ChangeUserStatusDtoRequest(userId, active);
	}

}
