package co.com.smartlife.smartlifeapi.shared.service.input.port;

import co.com.smartlife.smartlifeapi.shared.service.input.dto.request.CatalogWebhookDtoRequest;

/** Recibe los cambios de los catalogos de Strapi (mensajes y notificaciones) para limpiar su cache. */
public interface InvalidateCatalogCacheInputPort extends InputPortWithOutReturn<CatalogWebhookDtoRequest> {

}
