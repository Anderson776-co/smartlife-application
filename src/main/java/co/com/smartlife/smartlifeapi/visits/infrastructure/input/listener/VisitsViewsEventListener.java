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

/**
 * Escucha los eventos de los otros modulos y actualiza las vistas materializadas de este modulo.
 * Se ejecuta cuando el modulo que publico el evento ya confirmo su transaccion (AFTER_COMMIT) y en una transaccion
 * nueva: es lo mismo que hace @ApplicationModuleListener de Spring Modulith, sin la parte asincrona.
 */
@Component
public class VisitsViewsEventListener {

	private final RefreshVisitsViewsInputPort refreshViews;
	private final VisitsEventMapperInput mapper;

	public VisitsViewsEventListener(final RefreshVisitsViewsInputPort refreshViews, final VisitsEventMapperInput mapper) {
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

	/** Cambio informacion en el modulo residents. */
	@TransactionalEventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void on(final ResidentResidenceUpdatedEvent event) {
		refreshViews.execute(mapper.fromResidents(event));
	}
}
