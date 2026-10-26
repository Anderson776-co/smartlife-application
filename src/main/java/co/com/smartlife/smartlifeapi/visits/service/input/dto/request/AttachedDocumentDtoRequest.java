package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

public record AttachedDocumentDtoRequest(String fileName, String contentType, byte[] content) {

}
