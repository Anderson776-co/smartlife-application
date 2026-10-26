package co.com.smartlife.smartlifeapi.shared.service.input.port;

public interface InputPortWithReturn <T, R> {
	R execute(T data);

}
