package co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import co.com.smartlife.smartlifeapi.users.event.UserUpdatedEvent;
import co.com.smartlife.smartlifeapi.dwellings.event.DwellingUpdatedEvent;
import co.com.smartlife.smartlifeapi.residents.infrastructure.input.listener.mapper.ResidentsEventMapperInput;
import co.com.smartlife.smartlifeapi.residents.service.input.port.RefreshResidentsViewsInputPort;

/**
 * Escucha los eventos de los otros modulos y actualiza las vistas materializadas de este modulo.
 * Se ejecuta cuando el modulo que publico el evento ya confirmo su transaccion (AFTER_COMMIT) y en una transaccion
 * nueva: es lo mismo que hace @ApplicationModuleListener de Spring Modulith, sin la parte asincrona.
 */
@Component
public class ResidentsViewsEventListener {

	private final RefreshResidentsViewsInputPort refreshViews;
	private final ResidentsEventMapperInput mapper;

	public ResidentsViewsEventListener(final RefreshResidentsViewsInputPort refreshViews, final ResidentsEventMapperInput mapper) {
		this.refreshViews = refreshViews;
		this.mapper = mapper;
	}

	/** Cambio informacion en el modulo users. */
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final UserUpdatedEvent event) {
		refreshViews.execute(mapper.fromUsers(event));
	}

	/** Cambio informacion en el modulo dwellings. */
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final DwellingUpdatedEvent event) {
		refreshViews.execute(mapper.fromDwellings(event));
	}
}
