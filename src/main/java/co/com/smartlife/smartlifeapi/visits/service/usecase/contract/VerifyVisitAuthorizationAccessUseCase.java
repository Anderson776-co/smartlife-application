package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import co.com.smartlife.smartlifeapi.shared.service.usecase.UseCaseWithReturn;
import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationAccessDomain;

public interface VerifyVisitAuthorizationAccessUseCase extends UseCaseWithReturn<VisitAuthorizationAccessDomain, Boolean> {

}
