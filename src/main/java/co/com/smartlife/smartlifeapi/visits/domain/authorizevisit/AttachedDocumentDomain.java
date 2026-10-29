package co.com.smartlife.smartlifeapi.visits.domain.authorizevisit;

public record AttachedDocumentDomain(String fileName, String contentType, byte[] content) {

	private static final byte[] EMPTY_CONTENT = new byte[0];

	public static AttachedDocumentDomain empty() {
		return new AttachedDocumentDomain("", "", EMPTY_CONTENT);
	}

	public boolean hasContent() {
		return content.length > 0;
	}
	
}
