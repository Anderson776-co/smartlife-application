package co.com.smartlife.smartlifeapi.visits.infrastructure.input.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Control de acceso del caso de uso "autorizar visita".
 * <ul>
 * <li>RBAC: solo los roles OWNER y RESIDENT.</li>
 * <li>ABAC + ReBAC: delegado a {@link VisitAuthorizationAccessEvaluator}.</li>
 * </ul>
 * El metodo anotado debe recibir un parametro llamado {@code request} que exponga {@code propertyId()}.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('OWNER', 'RESIDENT') "
		+ "and @visitAuthorizationAccessEvaluator.canAuthorize(authentication, #request.propertyId())")
public @interface CanAuthorizeVisit {

}
