package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

public interface QrGeneratorOutputPort {

	String generateBase64(String content);
}