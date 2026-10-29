package co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.mapper.DtoMapper;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AttachedDocumentDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.AuthorizatorDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitorDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.VisitorDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;

@Mapper(componentModel = "spring")
public interface VisitAuthorizationMapperInput extends DtoMapper<AuthorizeVisitDtoRequest, VisitAuthorizationDtoResponse, VisitAuthorizationDomain>{
	
	@Override
    @Mapping(target = "id", ignore = true)              
    @Mapping(target = "registerDate", ignore = true)    
    @Mapping(target = "qrCode", ignore = true)         
    @Mapping(target = "fileReference", ignore = true)
    @Mapping(target = "notificationSent", ignore = true)
    VisitAuthorizationDomain toDomain(AuthorizeVisitDtoRequest request);

	@Override
	@Mapping(target = "authorizationId", source = "id")
	@Mapping(target = "qrUrl", source = "qrCode")
	VisitAuthorizationDtoResponse toDTO(VisitAuthorizationDomain domain);
	
	@Mapping(target = "id", ignore = true)
    VisitorDomain toVisitorDomain(VisitorDtoRequest visitor);
	
	AttachedDocumentDomain toAttachedDocumentDomain(AttachedDocumentDtoRequest document);

	@Mapping(target = "id", source = "userId")
	AuthorizatorDomain toAuthorizatorDomain(CurrentUser currentUser);
	
}
