package co.com.smartlife.smartlifeapi.users.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.users.infrastructure.input.listener.mapper.UsersEventMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.port.RefreshUsersViewsInputPort;

@Component
public class UsersViewsEventListener {

	private final RefreshUsersViewsInputPort refreshViews;
	private final UsersEventMapperInput mapper;

	public UsersViewsEventListener(final RefreshUsersViewsInputPort refreshViews, final UsersEventMapperInput mapper) {
		this.refreshViews = refreshViews;
		this.mapper = mapper;
	}

	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final UrbanizationUpdatedEvent event) {
		refreshViews.execute(mapper.fromUrbanization(event));
	}
}
