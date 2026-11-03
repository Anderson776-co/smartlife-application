package co.com.smartlife.smartlifeapi.urbanization.infrastructure.input.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.urbanization.service.input.dto.request.RenameUrbanizationDtoRequest;
import co.com.smartlife.smartlifeapi.urbanization.service.input.port.RenameUrbanizationInputPort;

@RestController
@RequestMapping(UrbanizationController.PATH)
public class UrbanizationController {

	static final String PATH = "/api/v1/urbanizations";

	private final RenameUrbanizationInputPort renameUrbanizationInputPort;

	public UrbanizationController(final RenameUrbanizationInputPort renameUrbanizationInputPort) {
		this.renameUrbanizationInputPort = renameUrbanizationInputPort;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{urbanizationId}")
	public ResponseEntity<Response<Void>> rename(
			@PathVariable("urbanizationId") final UUID urbanizationId,
			@RequestBody final RenameUrbanizationDtoRequest request) {

		renameUrbanizationInputPort.execute(request.withUrbanizationId(urbanizationId));

		return ResponseEntity.ok(Response.createSuccededResponse());
	}

}
