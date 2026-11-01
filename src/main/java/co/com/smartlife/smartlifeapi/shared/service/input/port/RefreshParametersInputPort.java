package co.com.smartlife.smartlifeapi.shared.service.input.port;

import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.ConfigWebhookDtoRequest;

/** Recibe los cambios del repositorio de parametros para recargar la configuracion. */
public interface RefreshParametersInputPort extends InputPortWithOutReturn<ConfigWebhookDtoRequest> {

}
