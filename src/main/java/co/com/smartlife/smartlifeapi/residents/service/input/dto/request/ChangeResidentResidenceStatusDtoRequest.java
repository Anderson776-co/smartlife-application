package co.com.smartlife.smartlifeapi.residents.service.input.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.smartlife.smartlifeapi.crosscutting.helper.UUIDHelper;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChangeResidentResidenceStatusDtoRequest(@JsonIgnore @Schema(hidden = true) UUID residentResidenceId, Boolean active) {

	public ChangeResidentResidenceStatusDtoRequest {
		residentResidenceId = UUIDHelper.getUUIDHelper().getDefault(residentResidenceId);
	}

	public ChangeResidentResidenceStatusDtoRequest withResidentResidenceId(final UUID residentResidenceId) {
		return new ChangeResidentResidenceStatusDtoRequest(residentResidenceId, active);
	}

}
