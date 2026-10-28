package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AttachedDocumentDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.AuthorizeVisitInputPort;

@RestController
@RequestMapping("visits/api/v1/authorizations")
public class VisitAuthorizationController {
	
	private final AuthorizeVisitInputPort authorizeVisitInputPort;

	public VisitAuthorizationController(final AuthorizeVisitInputPort authorizeVisitInputPort) {
		this.authorizeVisitInputPort = authorizeVisitInputPort;
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Response<VisitAuthorizationDtoResponse>> autorize (
			@RequestPart("authorization") final AuthorizeVisitDtoRequest request,
			@RequestPart(value = "document", required = false) final MultipartFile document) throws IOException {
		
		//Sanitizer
		
		
		final VisitAuthorizationDtoResponse result = authorizeVisitInputPort.execute(input);
		return ResponseEntity.status(HttpStatus.CREATED).body(Response.createSuccededResponse(List.of(result)));
				
	}
	
	private static AttachedDocumentDtoRequest toAttachedDocument(final MultipartFile document) throws IOException {
		if (document == null || document.isEmpty()) {
			return null;
		}
		return new AttachedDocumentDtoRequest(document.getOriginalFilename(), document.getContentType(), document.getBytes());
	}

}
