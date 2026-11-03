package co.com.smartlife.smartlifeapi.visits.service.input.port;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.GetVisitAuthorizationDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

public interface GetVisitAuthorizationInputPort
		extends InputPortWithReturn<GetVisitAuthorizationDtoRequest, VisitAuthorizationSummaryDtoResponse> {
}
