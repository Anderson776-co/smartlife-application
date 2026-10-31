package co.com.smartlife.smartlifeapi.visits.service.input.port;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.AuthorizeVisitDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;

public interface AuthorizeVisitInputPort extends InputPortWithReturn<AuthorizeVisitDtoRequest, VisitAuthorizationDtoResponse>{

}
