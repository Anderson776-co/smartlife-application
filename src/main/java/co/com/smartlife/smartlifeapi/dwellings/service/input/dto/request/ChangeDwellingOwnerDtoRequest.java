package co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeDwellingOwnerDtoRequest(@JsonIgnore @Schema(hidden = true) UUID dwellingId, UUID ownerId) {

	public ChangeDwellingOwnerDtoRequest {
		dwellingId = UUIDHelper.getUUIDHelper().getDefault(dwellingId);
		ownerId = UUIDHelper.getUUIDHelper().getDefault(ownerId);
	}

	/** El identificador sale de la direccion, no del cuerpo de la peticion. */
	public ChangeDwellingOwnerDtoRequest withDwellingId(final UUID dwellingId) {
		return new ChangeDwellingOwnerDtoRequest(dwellingId, ownerId);
	}

}
