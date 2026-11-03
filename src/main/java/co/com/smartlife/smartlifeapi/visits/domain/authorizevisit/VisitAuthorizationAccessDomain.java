package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.UUID;

public record VisitAuthorizationAccessDomain(AuthorizatorDomain authorizator, UUID propertyId) {

}
