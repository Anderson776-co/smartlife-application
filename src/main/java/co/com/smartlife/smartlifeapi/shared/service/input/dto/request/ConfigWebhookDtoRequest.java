package co.com.smartlife.smartlifeapi.shared.service.input.dto.request;

import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record ConfigWebhookDtoRequest(String event, String ref) {

	public ConfigWebhookDtoRequest {
		event = TextHelper.getDefaultWithTrim(event);
		ref = TextHelper.getDefaultWithTrim(ref);
	}
}
