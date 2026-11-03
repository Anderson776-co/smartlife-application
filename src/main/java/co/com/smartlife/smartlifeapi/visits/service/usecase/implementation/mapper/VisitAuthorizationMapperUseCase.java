package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.shared.mapper.DomainMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationStatus;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.AuthorizatorEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.PropertyVisitEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitReasonEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

@Mapper(componentModel = "spring", imports = { AttachedDocumentDomain.class, VisitAuthorizationStatus.class })
public interface VisitAuthorizationMapperUseCase extends DomainMapper<VisitAuthorizationDomain, VisitAuthorizationEntity> {

	@Mapping(target = "id", source = "authorization.id")
	@Mapping(target = "isActive", constant = "true")
	@Mapping(target = "property", source = "property")
	@Mapping(target = "authorizator", source = "authorizator")
	@Mapping(target = "visitor", source = "visitor")
	@Mapping(target = "visitReason", source = "visitReason")
	VisitAuthorizationEntity toEntity(VisitAuthorizationDomain authorization, PropertyVisitEntity property,
			AuthorizatorEntity authorizator, VisitorEntity visitor, VisitReasonEntity visitReason);

	@Mapping(target = "id", source = "authorization.id")
	@Mapping(target = "propertyId", source = "authorization.property.id")
	@Mapping(target = "visitReasonId", source = "authorization.visitReason.id")
	@Mapping(target = "fileReference", source = "authorization.fileReference", defaultValue = "")
	@Mapping(target = "attachedDocument", expression = "java(AttachedDocumentDomain.empty())")
	@Mapping(target = "authorizator", source = "authorizator")
	@Mapping(target = "notificationSent", source = "notificationSent")
	@Mapping(target = "propertyLocation", source = "authorization.property.location", defaultValue = "")
	@Mapping(target = "visitReasonName", source = "authorization.visitReason.name", defaultValue = "")
	@Mapping(target = "status", expression = "java(VisitAuthorizationStatus.UNSPECIFIED)")
	VisitAuthorizationDomain toDomain(VisitAuthorizationEntity authorization, AuthorizatorDomain authorizator,
			boolean notificationSent);

	@Mapping(target = "propertyId", source = "authorization.property.id")
	@Mapping(target = "visitReasonId", source = "authorization.visitReason.id")
	@Mapping(target = "observation", source = "authorization.observation", defaultValue = "")
	@Mapping(target = "fileReference", source = "authorization.fileReference", defaultValue = "")
	@Mapping(target = "attachedDocument", expression = "java(AttachedDocumentDomain.empty())")
	@Mapping(target = "notificationSent", constant = "false")
	@Mapping(target = "propertyLocation", source = "authorization.property.location")
	@Mapping(target = "visitReasonName", source = "authorization.visitReason.name")
	@Mapping(target = "status", source = "status")
	VisitAuthorizationDomain toDomain(VisitAuthorizationEntity authorization, VisitAuthorizationStatus status);

	@Mapping(target = "roles", expression = "java(java.util.Set.of())")
	AuthorizatorDomain toAuthorizatorDomain(AuthorizatorEntity authorizator);

	@Mapping(target = "vehiclePlate", source = "vehiclePlate", defaultValue = "")
	VisitorDomain toVisitorDomain(VisitorEntity visitor);

}
