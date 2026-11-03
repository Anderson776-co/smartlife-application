package co.com.smartlife.smartlifeapi.shared.infrastructure.output.adapter.events;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class SpringDomainEventPublisherAdapterTest {

	@Test
	void publishShouldHandTheEventToSpring() {
		final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
		final Object event = new Object();

		new SpringDomainEventPublisherAdapter(publisher).publish(event);

		verify(publisher).publishEvent(event);
	}
}
