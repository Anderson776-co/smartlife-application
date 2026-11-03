package co.com.smartlife.smartlifeapi.shared.service.output.port.events;

/**
 * Publica un evento para que otros modulos reaccionen (Spring Modulith). Quien publica no sabe
 * quien escucha: cada modulo interesado tiene su propio listener.
 */
public interface DomainEventPublisherOutputPort {

	void publish(Object event);

}
