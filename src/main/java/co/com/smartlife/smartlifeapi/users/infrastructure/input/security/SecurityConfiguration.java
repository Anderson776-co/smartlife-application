package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import co.com.smartlife.smartlifeapi.users.service.input.port.ValidateSessionInputPort;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
	private static final String UNAUTHORIZED = "{\"messages\":[\"Sesión requerida\"],\"data\":[],\"responseSucceded\":false}";
    private static final String FORBIDDEN = "{\"messages\":[\"Acceso denegado\"],\"data\":[],\"responseSucceded\":false}";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ValidateSessionInputPort validateSession,
            SessionCookieManager cookies) throws Exception {
        http
            .csrf(csrf -> csrf.spa())   // cookie XSRF-TOKEN + header X-XSRF-TOKEN
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/users/sessions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/users/sessions/csrf").permitAll()
                .requestMatchers("/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> write(res, HttpStatus.UNAUTHORIZED, UNAUTHORIZED))
                .accessDeniedHandler((req, res, ex) -> write(res, HttpStatus.FORBIDDEN, FORBIDDEN)))
            .addFilterBefore(new SessionCookieFilter(validateSession, cookies),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Solo hace falta si front y API son orígenes distintos (pero del mismo sitio)
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${smartlife.cors.allowed-origins}") List<String> origins) {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
        config.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static void write(HttpServletResponse res, HttpStatus status, String body) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(body);
    }
}
