package co.com.smartlife.smartlifeapi.shared.service.input.port;

public interface MessageResolverInputPort {

	String resolve(String code, String fallback);

}
