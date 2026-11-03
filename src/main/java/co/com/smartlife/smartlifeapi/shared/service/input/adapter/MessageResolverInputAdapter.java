package co.com.smartlife.smartlifeapi.shared.service.input.adapter;

import org.springframework.stereotype.Service;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.shared.service.input.port.MessageResolverInputPort;
import co.com.smartlife.smartlifeapi.shared.service.output.port.externalservices.messagecatalog.MessageCatalogProvider;

@Service
public class MessageResolverInputAdapter implements MessageResolverInputPort {

	@Override
	public String resolve(final String code, final String fallback) {
		final String content = MessageCatalogProvider.getContent(code);
		return ObjectHelper.isNull(content) || content.isBlank() || content.equals(code) ? fallback : content;
	}

}
