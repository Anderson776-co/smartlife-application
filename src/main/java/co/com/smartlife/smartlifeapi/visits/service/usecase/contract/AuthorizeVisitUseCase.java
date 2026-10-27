package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import co.com.smartlife.smartlifeapi.shared.service.usecase.UseCaseWithReturn;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationDtoResponse;

public interface AuthorizeVisitUseCase extends UseCaseWithReturn<VisitAuthorizationDomain, VisitAuthorizationDtoResponse>{

}
