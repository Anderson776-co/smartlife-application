package co.com.smartlife.smartlifeapi.visits.service.input.port;

import java.util.List;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;

public interface ListVisitAuthorizationsInputPort
        extends InputPortWithReturn<ListVisitAuthorizationsDtoRequest, List<VisitAuthorizationSummaryDtoResponse>> {
}
