package co.com.smartlife.smartlifeapi.visits.service.input.port;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationPageDtoResponse;

public interface ListVisitAuthorizationsInputPort
		extends InputPortWithReturn<ListVisitAuthorizationsDtoRequest, VisitAuthorizationPageDtoResponse> {
}
