package co.com.smartlife.smartlifeapi.visits.service.input.adapter;

import java.util.List;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.visits.domain.authorizevisit.VisitAuthorizationFilterDomain;
import co.com.smartlife.smartlifeapi.visits.service.input.adapter.mapper.VisitAuthorizationSummaryDtoMapper;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.request.ListVisitAuthorizationsDtoRequest;
import co.com.smartlife.smartlifeapi.visits.service.input.dto.response.VisitAuthorizationSummaryDtoResponse;
import co.com.smartlife.smartlifeapi.visits.service.input.port.ListVisitAuthorizationsInputPort;
import co.com.smartlife.smartlifeapi.visits.service.usecase.contract.ConsultVisitAuthorizationsUseCase;

@Service
public class ListVisitAuthorizationsInputAdapter implements ListVisitAuthorizationsInputPort {

    private final ConsultVisitAuthorizationsUseCase useCase;

    public ListVisitAuthorizationsInputAdapter(final ConsultVisitAuthorizationsUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public List<VisitAuthorizationSummaryDtoResponse> execute(final ListVisitAuthorizationsDtoRequest data) {
        final VisitAuthorizationFilterDomain filter = new VisitAuthorizationFilterDomain(data.status(), data.from(),
                data.to(), data.propertyId());

        return useCase.list(data.currentUser().userId(), filter).stream()
                .map(VisitAuthorizationSummaryDtoMapper::toDto)
                .toList();
    }
}
