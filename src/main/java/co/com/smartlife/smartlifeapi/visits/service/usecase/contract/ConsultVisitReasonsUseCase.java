package co.com.smartlife.smartlifeapi.visits.service.usecase.contract;

import java.util.List;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitReasonDomain;

public interface ConsultVisitReasonsUseCase {

	List<VisitReasonDomain> list();

}
