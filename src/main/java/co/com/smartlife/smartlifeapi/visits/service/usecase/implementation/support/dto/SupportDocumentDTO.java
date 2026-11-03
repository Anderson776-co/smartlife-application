package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.dto;

import java.util.Arrays;
import java.util.Objects;
import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;

public record SupportDocumentDTO(String fileName, String contentType, byte[] content) {

	private static final byte[] EMPTY_CONTENT = new byte[0];

	public SupportDocumentDTO {
		content = ObjectHelper.getDefault(content, EMPTY_CONTENT);
	}
	public boolean hasContent() {
		return content.length > 0;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SupportDocumentDTO that)) {
			return false;
		}
		return Objects.equals(fileName, that.fileName) && Objects.equals(contentType, that.contentType)
				&& Arrays.equals(content, that.content);
	}

	@Override
	public int hashCode() {
		return 31 * Objects.hash(fileName, contentType) + Arrays.hashCode(content);
	}

	@Override
	public String toString() {
		return "SupportDocumentDTO[fileName=" + fileName + ", contentType=" + contentType + ", contentLength="
				+ content.length + "]";
	}
}