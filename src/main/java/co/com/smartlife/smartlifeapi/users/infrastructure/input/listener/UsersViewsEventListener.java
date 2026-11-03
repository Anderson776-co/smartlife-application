package co.com.smartlife.smartlifeapi.users.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.urbanization.event.UrbanizationUpdatedEvent;
import co.com.smartlife.smartlifeapi.users.infrastructure.input.listener.mapper.UsersEventMapperInput;
import co.com.smartlife.smartlifeapi.users.service.input.port.RefreshUsersViewsInputPort;

/**
 * Escucha los eventos de los otros modulos y actualiza las vistas materializadas de este modulo.
 * Se ejecuta cuando el modulo que publico el evento ya confirmo su transaccion (AFTER_COMMIT) y en una transaccion
 * nueva: es lo mismo que hace @ApplicationModuleListener de Spring Modulith, sin la parte asincrona.
 */
@Component
public class UsersViewsEventListener {

	private final RefreshUsersViewsInputPort refreshViews;
	private final UsersEventMapperInput mapper;

	public UsersViewsEventListener(final RefreshUsersViewsInputPort refreshViews, final UsersEventMapperInput mapper) {
		this.refreshViews = refreshViews;
		this.mapper = mapper;
	}

	/** Cambio informacion en el modulo urbanization. */
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final UrbanizationUpdatedEvent event) {
		refreshViews.execute(mapper.fromUrbanization(event));
	}
}
