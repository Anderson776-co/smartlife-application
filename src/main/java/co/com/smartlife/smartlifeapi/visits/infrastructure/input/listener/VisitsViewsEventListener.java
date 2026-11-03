package co.com.smartlife.smartlifeapi.visits.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.event.ResidentResidenceUpdatedEvent;
import co.com.smartlife.smartlifeapi.visits.infrastructure.input.listener.mapper.VisitsEventMapperInput;
import co.com.smartlife.smartlifeapi.visits.service.input.port.RefreshVisitsViewsInputPort;

@Component
public class VisitsViewsEventListener {

	private final RefreshVisitsViewsInputPort refreshViews;
	private final VisitsEventMapperInput mapper;

	public VisitsViewsEventListener(final RefreshVisitsViewsInputPort refreshViews, final VisitsEventMapperInput mapper) {
		this.refreshViews = refreshViews;
		this.mapper = mapper;
	}

	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final UserUpdatedEvent event) {
		refreshViews.execute(mapper.fromUsers(event));
	}

	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final DwellingUpdatedEvent event) {
		refreshViews.execute(mapper.fromDwellings(event));
	}

	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final ResidentResidenceUpdatedEvent event) {
		refreshViews.execute(mapper.fromResidents(event));
	}
}
