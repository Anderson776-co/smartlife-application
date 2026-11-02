package co.com.smartlife.smartlifeapi.users.infrastructure.input.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private static final String WEBHOOKS = "/api/v1/internal/webhooks/**";

    /**
     * Cadena propia de los webhooks entrantes (Strapi y GitHub). Es sin estado, no lee la cookie de sesion
     * (no lleva {@link SessionCookieFilter}) y cada webhook se autentica con su secreto o firma en el
     * controlador. Como estas peticiones no dependen de una sesion del navegador, no hay nada que un ataque
     * CSRF pueda aprovechar, y el resto de la API conserva la proteccion CSRF completa.
     */
    @SuppressWarnings("java:S4502")   // excepcion de CSRF declarada y justificada: ver la explicacion de arriba
    @Bean
    @Order(1)
    SecurityFilterChain webhookFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(WEBHOOKS)
            .csrf(AbstractHttpConfigurer::disable)   // NOSONAR java:S4502 - solo en la cadena de webhooks: sin sesion ni cookie, se autentican con secreto o firma
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, WEBHOOKS).permitAll()   // el controlador valida el secreto de cada webhook
                .anyRequest().denyAll());
        return http.build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ValidateSessionInputPort validateSession,
            SessionCookieManager cookies, SecurityErrorResponseWriter errors) throws Exception {
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
                .authenticationEntryPoint((req, res, ex) -> errors.unauthorized(res))
                .accessDeniedHandler((req, res, ex) -> errors.forbidden(res)))
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
}