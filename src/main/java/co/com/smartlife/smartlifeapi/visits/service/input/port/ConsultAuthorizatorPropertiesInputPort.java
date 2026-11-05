package co.com.smartlife.smartlifeapi.visits.service.input.port;

import java.util.List;

import co.com.smartlife.smartlifeapi.shared.service.input.port.InputPortWithReturn;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListAuthorizatorPropertiesDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.PropertyVisitDtoResponse;

public interface ConsultAuthorizatorPropertiesInputPort
		extends InputPortWithReturn<ListAuthorizatorPropertiesDtoRequest, List<PropertyVisitDtoResponse>> {
}
