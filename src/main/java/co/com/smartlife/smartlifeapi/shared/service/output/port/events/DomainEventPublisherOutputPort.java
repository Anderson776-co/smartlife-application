package co.com.smartlife.smartlifeapi.shared.service.output.port.events;

public interface DomainEventPublisherOutputPort {

	void publish(Object event);

}
