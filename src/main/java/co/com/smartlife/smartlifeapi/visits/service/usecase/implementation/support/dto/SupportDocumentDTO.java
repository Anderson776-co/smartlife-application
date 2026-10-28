package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support.dto;

public record SupportDocumentDTO(String fileName, String contentType, byte[] content) {
	public boolean hasContent() {
		return content.length > 0;
	}
}
