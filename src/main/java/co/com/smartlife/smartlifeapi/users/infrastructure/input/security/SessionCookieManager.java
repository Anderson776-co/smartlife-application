package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import co.com.smartlife.smartlifeapi.users.service.input.dto.response.SessionDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SessionCookieManager {
	
	private final String cookieName;
	
	 public SessionCookieManager(
	            @Value("${smartlife.session.cookie-name:__Host-smartlife_session}") String cookieName) {
	        this.cookieName = cookieName;
	 }
	 
	 public ResponseCookie create(final SessionDto session) {
	        return base(session.cookieValue()).maxAge(session.expiresIn()).build();
	    }

	    public ResponseCookie clear() {
	        return base("").maxAge(0).build();
	    }

	    public Optional<String> read(final HttpServletRequest request) {
	        final Cookie cookie = WebUtils.getCookie(request, cookieName);
	        return Optional.ofNullable(cookie).map(Cookie::getValue).filter(v -> !v.isBlank());
	    }

	    private ResponseCookie.ResponseCookieBuilder base(final String value) {
	        return ResponseCookie.from(cookieName, value)
	                .httpOnly(true)        // JS no puede leerla (protege contra XSS)
	                .secure(true)          // solo HTTPS
	                .sameSite("Strict")    // no viaja en requests iniciados por otros sitios
	                .path("/");
	    }

}
