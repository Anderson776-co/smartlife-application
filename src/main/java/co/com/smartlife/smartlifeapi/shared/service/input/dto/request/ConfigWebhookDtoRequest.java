package co.com.smartlife.smartlifeapi.shared.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/** Webhook del repositorio de parametros: el tipo de evento (cabecera de GitHub) y la referencia ({@code ref}) afectada. */
public record ConfigWebhookDtoRequest(String event, String ref) {

	public ConfigWebhookDtoRequest {
		event = TextHelper.getDefaultWithTrim(event);
		ref = TextHelper.getDefaultWithTrim(ref);
	}
}
