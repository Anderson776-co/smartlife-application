package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

import java.util.Arrays;
import java.util.Objects;

public record AttachedDocumentDomain(String fileName, String contentType, byte[] content) {

	private static final byte[] EMPTY_CONTENT = new byte[0];

	public static AttachedDocumentDomain empty() {
		return new AttachedDocumentDomain("", "", EMPTY_CONTENT);
	}

	public boolean hasContent() {
		return content.length > 0;
	}

	
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AttachedDocumentDomain that)) {
			return false;
		}
		return Objects.equals(fileName, that.fileName) && Objects.equals(contentType, that.contentType)
				&& Arrays.equals(content, that.content);
	}

	@Override
	public int hashCode() {
		return 31 * Objects.hash(fileName, contentType) + Arrays.hashCode(content);
	}

	
	public String toString() {
		return "AttachedDocumentDomain[fileName=" + fileName + ", contentType=" + contentType + ", contentLength="
				+ (content == null ? 0 : content.length) + "]";
	}
}