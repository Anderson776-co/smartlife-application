package co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener.mapper.ResidentsEventMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.port.RefreshResidentsViewsInputPort;

@Component
public class ResidentsViewsEventListener {

	private final RefreshResidentsViewsInputPort refreshViews;
	private final ResidentsEventMapperInput mapper;

	public ResidentsViewsEventListener(final RefreshResidentsViewsInputPort refreshViews, final ResidentsEventMapperInput mapper) {
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
}
