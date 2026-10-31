package co.com.smartlife.smartlifeapi.users.infrastructure.input.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUserHolder;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.users.infrastructure.input.security.SessionCookieManager;
import co.com.smartlife.smartlifeapi.users.service.input.dto.request.LoginDtoRequest;
import co.com.smartlife.smartlifeapi.users.service.input.dto.response.SessionDto;
import co.com.smartlife.smartlifeapi.users.service.input.port.LogOutInputPort;
import co.com.smartlife.smartlifeapi.users.service.input.port.LoginInputPort;


@RestController
@RequestMapping("/api/v1/users/sessions")
public class SessionController {
	
	private final LoginInputPort loginInputPort;
	private final LogOutInputPort logOutInputPort;
	private final SessionCookieManager cookies;
	
	public SessionController(LoginInputPort loginInputPort, LogOutInputPort logOutInputPort,
			SessionCookieManager cookies) {
		this.loginInputPort = loginInputPort;
		this.logOutInputPort = logOutInputPort;
		this.cookies = cookies;
	}
	
	@GetMapping("/csrf")
	public ResponseEntity<Void> csrf(final CsrfToken token) {
		token.getToken();
        return ResponseEntity.noContent().build();
	}
	
	@PostMapping
	public ResponseEntity<Response<Void>> login (@RequestBody final LoginDtoRequest request) {
		final SessionDto session = loginInputPort.execute(request.idToken());
		return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.create(session).toString())
                .body(Response.createSuccededResponse());
	}
	
	@GetMapping("/me")   
    public ResponseEntity<Response<CurrentUser>> me() {
        return CurrentUserHolder.get()
                .map(user -> ResponseEntity.ok(Response.createSuccededResponse(List.of(user))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.createFailedResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout() {
        CurrentUserHolder.get().ifPresent(user -> logOutInputPort.execute(user.userId()));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.clear().toString())
                .body(Response.createSuccededResponse());
    }

}
