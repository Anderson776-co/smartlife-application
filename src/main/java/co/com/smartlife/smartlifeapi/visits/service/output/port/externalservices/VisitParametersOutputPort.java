package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.VisitParametersDTO;

public interface VisitParametersOutputPort {

	/** Siempre entrega los valores vigentes: un refresh de la configuracion se refleja sin reiniciar. */
	VisitParametersDTO get();

}
