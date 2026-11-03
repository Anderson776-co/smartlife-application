package co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public record RenameUrbanizationDtoRequest(@JsonIgnore @Schema(hidden = true) UUID urbanizationId, String name) {

	public RenameUrbanizationDtoRequest {
		urbanizationId = UUIDHelper.getUUIDHelper().getDefault(urbanizationId);
		name = TextHelper.getDefaultWithTrim(name);
	}

	public RenameUrbanizationDtoRequest withUrbanizationId(final UUID urbanizationId) {
		return new RenameUrbanizationDtoRequest(urbanizationId, name);
	}

}
