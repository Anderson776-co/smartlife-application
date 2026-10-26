package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUserHolder;
import co.com.smartlife.smartlifeapi.users.service.input.port.ValidateSessionInputPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class SessionCookieFilter extends OncePerRequestFilter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionCookieFilter.class);
    private final ValidateSessionInputPort validateSession;
    private final SessionCookieManager cookies;
    
    

	public SessionCookieFilter(final ValidateSessionInputPort validateSession, final SessionCookieManager cookies) {
		this.validateSession = validateSession;
		this.cookies = cookies;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		cookies.read(request).ifPresent(value -> authenticate(value, response));
        filterChain.doFilter(request, response);	
	}
	
	private void authenticate(final String cookieValue, final HttpServletResponse response) {
        try {
            final CurrentUser user = validateSession.execute(cookieValue);
            final var authorities = user.roles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
            final SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(user, null, authorities));
            SecurityContextHolder.setContext(context);
            CurrentUserHolder.set(user);          // ← así lo comparte con los otros módulos
        } catch (SmartLifeException exception) {
            LOGGER.debug("Sesión rechazada: {}", exception.getTechnicalMessage());
            SecurityContextHolder.clearContext();
            response.addHeader(HttpHeaders.SET_COOKIE, cookies.clear().toString());
            // sin autenticación, Spring Security responderá 401 en las rutas protegidas
        }
    }
}
