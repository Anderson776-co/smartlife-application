package co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.infrastructure.input.listener.mapper.DwellingsEventMapperInput;
import co.com.smartlife.smartlifeapi.dwellings.service.input.port.RefreshDwellingsViewsInputPort;

@Component
public class DwellingsViewsEventListener {

	private final RefreshDwellingsViewsInputPort refreshViews;
	private final DwellingsEventMapperInput mapper;

	public DwellingsViewsEventListener(final RefreshDwellingsViewsInputPort refreshViews, final DwellingsEventMapperInput mapper) {
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
	public void on(final UrbanizationUpdatedEvent event) {
		refreshViews.execute(mapper.fromUrbanization(event));
	}
}
