package co.com.smartlife.smartlifeapi.shared.service.input.port;

/** Entrega a la capa de entrada el texto de un mensaje del catalogo (Strapi). */
public interface MessageResolverInputPort {

	/** Mensaje del catalogo para el codigo; si no se puede resolver, el texto de respaldo (nunca el codigo). */
	String resolve(String code, String fallback);

}
