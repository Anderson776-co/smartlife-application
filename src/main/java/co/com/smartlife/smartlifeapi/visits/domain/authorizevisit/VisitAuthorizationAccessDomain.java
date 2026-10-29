package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

/**
 * Solicitud de acceso al caso de uso "autorizar visita": quien intenta autorizar
 * y sobre que propiedad.
 */
public record VisitAuthorizationAccessDomain(AuthorizatorDomain authorizator, UUID propertyId) {

}
