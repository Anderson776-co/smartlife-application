package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.telemetry.micrometer.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class MicrometerTelemetryAdapterTest {

	private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
	private final MicrometerTelemetryAdapter adapter = new MicrometerTelemetryAdapter(registry);

	@Test
	void incrementCounterShouldAddOneEachTime() {
		adapter.incrementCounter("visit.authorizations");
		adapter.incrementCounter("visit.authorizations");

		assertEquals(2.0, registry.counter("visit.authorizations").count());
	}

	@Test
	void countersWithDifferentNamesShouldBeIndependent() {
		adapter.incrementCounter("visit.authorizations");
		adapter.incrementCounter("visit.rejections");

		assertEquals(1.0, registry.counter("visit.authorizations").count());
		assertEquals(1.0, registry.counter("visit.rejections").count());
	}

	@Test
	void recordDurationShouldAccumulateTheTime() {
		adapter.recordDuration("visit.registration.time", Duration.ofMillis(200));
		adapter.recordDuration("visit.registration.time", Duration.ofMillis(300));

		assertEquals(2, registry.timer("visit.registration.time").count());
		assertEquals(500.0, registry.timer("visit.registration.time").totalTime(TimeUnit.MILLISECONDS));
	}
}