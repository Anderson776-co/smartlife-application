package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.shared.service.output.port.events.DomainEventPublisherOutputPort;

@Component
public class SpringDomainEventPublisherAdapter implements DomainEventPublisherOutputPort {

	private final ApplicationEventPublisher publisher;

	public SpringDomainEventPublisherAdapter(final ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public void publish(final Object event) {
		publisher.publishEvent(event);
	}

}
