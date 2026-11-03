package co.com.smartlife.smartlifeapi.residents.infrastructure.input.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.residents.service.input.dto.request.ChangeResidentResidenceStatusDtoRequest;
import co.com.smartlife.smartlifeapi.residents.service.input.port.ChangeResidentResidenceStatusInputPort;

@RestController
@RequestMapping(ResidentResidenceController.PATH)
public class ResidentResidenceController {

	static final String PATH = "/api/v1/residents/residences";

	private final ChangeResidentResidenceStatusInputPort changeResidentResidenceStatusInputPort;

	public ResidentResidenceController(final ChangeResidentResidenceStatusInputPort changeResidentResidenceStatusInputPort) {
		this.changeResidentResidenceStatusInputPort = changeResidentResidenceStatusInputPort;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{residentResidenceId}/status")
	public ResponseEntity<Response<Void>> changeStatus(
			@PathVariable("residentResidenceId") final UUID residentResidenceId,
			@RequestBody final ChangeResidentResidenceStatusDtoRequest request) {

		changeResidentResidenceStatusInputPort.execute(request.withResidentResidenceId(residentResidenceId));

		return ResponseEntity.ok(Response.createSuccededResponse());
	}

}
