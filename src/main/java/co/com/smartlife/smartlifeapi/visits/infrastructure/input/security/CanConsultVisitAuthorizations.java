package co.com.smartlife.smartlifeapi.visits.infrastructure.input.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Control de acceso de la consulta de autorizaciones: solo los roles OWNER y RESIDENT. Cada persona consulta
 * unicamente las autorizaciones que ella creo: la persona sale de la sesion y la consulta la filtra por ella.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('OWNER', 'RESIDENT')")
public @interface CanConsultVisitAuthorizations {
}
