package co.com.smartlife.smartlifeapi.visits.infrastructure.input.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.security.CanConsultVisitAuthorizations;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ConsultVisitReasonsInputPort;

@RestController
@RequestMapping(VisitReasonController.PATH)
public class VisitReasonController {

	static final String PATH = "/api/v1/visits/reasons";

	private final ConsultVisitReasonsInputPort consultVisitReasonsInputPort;

	public VisitReasonController(final ConsultVisitReasonsInputPort consultVisitReasonsInputPort) {
		this.consultVisitReasonsInputPort = consultVisitReasonsInputPort;
	}

	@CanConsultVisitAuthorizations
	@GetMapping
	public ResponseEntity<Response<VisitReasonDtoResponse>> list() {
		return ResponseEntity.ok(Response.createSuccededResponse(consultVisitReasonsInputPort.execute()));
	}

}
