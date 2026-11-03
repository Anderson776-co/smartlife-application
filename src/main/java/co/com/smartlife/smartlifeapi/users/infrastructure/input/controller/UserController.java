package co.com.smartlife.smartlifeapi.users.infrastructure.input.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.ChangeUserStatusDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.port.ChangeUserStatusInputPort;

@RestController
@RequestMapping(UserController.PATH)
public class UserController {

	static final String PATH = "/api/v1/users";

	private final ChangeUserStatusInputPort changeUserStatusInputPort;

	public UserController(final ChangeUserStatusInputPort changeUserStatusInputPort) {
		this.changeUserStatusInputPort = changeUserStatusInputPort;
	}

	/** Solo el administrador cambia estos datos. Al terminar se publica un evento que actualiza las vistas. */
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{userId}/status")
	public ResponseEntity<Response<Void>> changeStatus(
			@PathVariable("userId") final UUID userId,
			@RequestBody final ChangeUserStatusDtoRequest request) {

		changeUserStatusInputPort.execute(request.withUserId(userId));

		return ResponseEntity.ok(Response.createSuccededResponse());
	}

}
