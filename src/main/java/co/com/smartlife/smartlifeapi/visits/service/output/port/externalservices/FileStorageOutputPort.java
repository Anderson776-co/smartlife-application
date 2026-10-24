package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

public interface FileStorageOutputPort {

	String upload(String key, byte[] content, String contentType);

	byte[] download(String key);
}