package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.crosscutting.session.CurrentUser;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.security.CanConsultVisitAuthorizations;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultAuthorizatorPropertiesInputPort;

@RestController
@RequestMapping(PropertyVisitController.PATH)
public class PropertyVisitController {

	static final String PATH = "/api/v1/visits/properties";

	private final ConsultAuthorizatorPropertiesInputPort consultAuthorizatorPropertiesInputPort;

	public PropertyVisitController(final ConsultAuthorizatorPropertiesInputPort listAuthorizablePropertiesInputPort) {
		this.consultAuthorizatorPropertiesInputPort = listAuthorizablePropertiesInputPort;
	}

	
	@CanConsultVisitAuthorizations
	@GetMapping
	public ResponseEntity<Response<PropertyVisitDtoResponse>> list(@AuthenticationPrincipal final CurrentUser currentUser) {
		return ResponseEntity.ok(
				Response.createSuccededResponse(
						consultAuthorizatorPropertiesInputPort.execute(new ListAuthorizatorPropertiesDtoRequest(currentUser))
				)
		);
	}

}
