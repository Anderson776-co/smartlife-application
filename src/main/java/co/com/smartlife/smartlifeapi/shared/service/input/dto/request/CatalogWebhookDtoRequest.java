package co.com.smartlife.smartlifeapi.shared.service.input.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

/**
 * Cuerpo del webhook de Strapi: {@code event} (por ejemplo {@code entry.publish}), {@code model}
 * (nombre del catalogo) y la entrada afectada. Los demas campos del cuerpo se ignoran.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogWebhookDtoRequest(String event, String model, Entry entry) {

	public CatalogWebhookDtoRequest {
		event = TextHelper.getDefaultWithTrim(event);
		model = TextHelper.getDefaultWithTrim(model);
		entry = ObjectHelper.getDefault(entry, Entry.empty());
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Entry(String code, String locale) {

		public Entry {
			code = TextHelper.getDefaultWithTrim(code);
			locale = TextHelper.getDefaultWithTrim(locale);
		}

		public static Entry empty() {
			return new Entry(null, null);
		}
	}
}
