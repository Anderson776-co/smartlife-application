package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.util.Locale;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;

public record AttachedDocumentDtoRequest(String fileName, String contentType, byte[] content) {
	
	private static final byte[] EMPTY_CONTENT = new byte[0];
	
	public AttachedDocumentDtoRequest {
		fileName = TextHelper.getDefaultWithTrim(fileName);
		contentType = TextHelper.getDefaultWithTrim(contentType).toLowerCase(Locale.ROOT);
		content = ObjectHelper.getDefault(content, EMPTY_CONTENT);
	}
	
	public static AttachedDocumentDtoRequest empty() {
		return new AttachedDocumentDtoRequest(TextHelper.getDefault(), TextHelper.getDefault(), EMPTY_CONTENT);
	}
	
	public boolean hasContent() {
		return content.length > 0;
	}
}
