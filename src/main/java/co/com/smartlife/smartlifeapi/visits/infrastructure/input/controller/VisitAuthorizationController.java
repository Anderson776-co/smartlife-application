package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.security.CanAuthorizeVisit;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;

@RestController
@RequestMapping(VisitAuthorizationController.PATH)
public class VisitAuthorizationController {
	
	static final String PATH = "/api/v1/visits/authorizations";
	
	private final AuthorizeVisitInputPort authorizeVisitInputPort;

	public VisitAuthorizationController(final AuthorizeVisitInputPort authorizeVisitInputPort) {
		this.authorizeVisitInputPort = authorizeVisitInputPort;
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
	
	private static AttachedDocumentDtoRequest toAttachedDocument(final MultipartFile document) throws IOException {
		if (document == null || document.isEmpty()) {
			return null;
		}
		return new AttachedDocumentDtoRequest(document.getOriginalFilename(), document.getContentType(), document.getBytes());
	}

}