package co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.dwellings.service.input.dto.request.ChangeDwellingOwnerDtoRequest;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.ChangeDwellingOwnerInputPort;

@RestController
@RequestMapping(DwellingController.PATH)
public class DwellingController {

	static final String PATH = "/api/v1/dwellings";

	private final ChangeDwellingOwnerInputPort changeDwellingOwnerInputPort;

	public DwellingController(final ChangeDwellingOwnerInputPort changeDwellingOwnerInputPort) {
		this.changeDwellingOwnerInputPort = changeDwellingOwnerInputPort;
	}

	/** Solo el administrador cambia estos datos. Al terminar se publica un evento que actualiza las vistas. */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{dwellingId}/owner")
	public ResponseEntity<Response<Void>> changeOwner(
			@PathVariable("dwellingId") final UUID dwellingId,
			@RequestBody final ChangeDwellingOwnerDtoRequest request) {

		changeDwellingOwnerInputPort.execute(request.withDwellingId(dwellingId));

		return ResponseEntity.ok(Response.createSuccededResponse());
	}

}
