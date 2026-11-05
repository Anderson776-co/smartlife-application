package co.com.smartlife.smartlifeapi.visits.service.input.port;

import java.util.List;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithoutParameters;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitReasonDtoResponse;

public interface ConsultVisitReasonsInputPort extends InputPortWithoutParameters<List<VisitReasonDtoResponse>> {
}
