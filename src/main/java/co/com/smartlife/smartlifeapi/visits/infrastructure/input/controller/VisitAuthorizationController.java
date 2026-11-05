package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.security.CanAuthorizeVisit;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.security.CanConsultVisitAuthorizations;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;
import co.com.smartlife.smartlifeapi.visits.service.input.port.GetVisitAuthorizationInputPort;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ListVisitAuthorizationsInputPort;

@RestController
@RequestMapping(VisitAuthorizationController.PATH)
public class VisitAuthorizationController {
	
	static final String PATH = "/api/v1/visits/authorizations";
	
	private final AuthorizeVisitInputPort authorizeVisitInputPort;
	private final ListVisitAuthorizationsInputPort listVisitAuthorizationsInputPort;
	private final GetVisitAuthorizationInputPort getVisitAuthorizationInputPort;

	public VisitAuthorizationController(final AuthorizeVisitInputPort authorizeVisitInputPort,
			final ListVisitAuthorizationsInputPort listVisitAuthorizationsInputPort,
			final GetVisitAuthorizationInputPort getVisitAuthorizationInputPort) {
		this.authorizeVisitInputPort = authorizeVisitInputPort;
		this.listVisitAuthorizationsInputPort = listVisitAuthorizationsInputPort;
		this.getVisitAuthorizationInputPort = getVisitAuthorizationInputPort;
	}
	
	@CanAuthorizeVisit
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Response<VisitAuthorizationDtoResponse>> autorize (
			@RequestPart("authorization") final AuthorizeVisitDtoRequest request,
			@RequestPart(value = "document", required = false) final MultipartFile document,
			@AuthenticationPrincipal final CurrentUser currentUser,
			final UriComponentsBuilder uriBuilder) throws IOException {
		
		VisitAuthorizationDtoResponse result = authorizeVisitInputPort.execute(request.withContext(toAttachedDocument(document), currentUser));
		
		URI location = uriBuilder
				.path(PATH + "/{authorizationId}")
				.buildAndExpand(result.authorizationId())
				.toUri();

		return ResponseEntity.created(location)
				.body(Response.createSuccededResponse(List.of(result)));
	}
	
	
	@CanConsultVisitAuthorizations
	@GetMapping
	public ResponseEntity<Response<VisitAuthorizationPageDtoResponse>> list(
			@RequestParam(value = "isActive", required = false) final Boolean isActive,
			@RequestParam(value = "from", required = false) final Instant from,
			@RequestParam(value = "to", required = false) final Instant to,
			@RequestParam(value = "propertyId", required = false) final UUID propertyId,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "size", defaultValue = "10") final int size,
			@AuthenticationPrincipal final CurrentUser currentUser) {
		
		VisitAuthorizationPageDtoResponse authorizations = listVisitAuthorizationsInputPort
				.execute(new ListVisitAuthorizationsDtoRequest(isActive, from, to, propertyId, currentUser, page, size));
		
		return ResponseEntity.ok(Response.createSuccededResponse(List.of(authorizations)));
	}
	
	@CanConsultVisitAuthorizations
	@GetMapping("/{authorizationId}")
	public ResponseEntity<Response<VisitAuthorizationSummaryDtoResponse>> find(
			@PathVariable("authorizationId") final UUID authorizationId,
			@AuthenticationPrincipal final CurrentUser currentUser) {
		
		VisitAuthorizationSummaryDtoResponse authorization = getVisitAuthorizationInputPort
				.execute(new GetVisitAuthorizationDtoRequest(authorizationId, currentUser));
		
		return ResponseEntity.ok(Response.createSuccededResponse(List.of(authorization)));
	}
	
	private static AttachedDocumentDtoRequest toAttachedDocument(final MultipartFile document) throws IOException {
		if (ObjectHelper.isNull(document) || document.isEmpty()) {
			return AttachedDocumentDtoRequest.empty();
		}
		return new AttachedDocumentDtoRequest(document.getOriginalFilename(), document.getContentType(), document.getBytes());
	}

}
