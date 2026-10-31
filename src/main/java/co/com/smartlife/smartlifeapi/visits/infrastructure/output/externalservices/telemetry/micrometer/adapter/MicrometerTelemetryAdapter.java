package co.com.smartlife.smartlifeapi.visits.infrastructure.output.externalservices.telemetry.micrometer.adapter;

import java.time.Duration;

import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.visits.service.output.port.TelemetryOutputPort;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class MicrometerTelemetryAdapter implements TelemetryOutputPort {

	private final MeterRegistry meterRegistry;

	public MicrometerTelemetryAdapter(final MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@Override
	public void incrementCounter(final String name) {
		meterRegistry.counter(name).increment();
	}

	@Override
	public void recordDuration(final String name, final Duration duration) {
		meterRegistry.timer(name).record(duration);
	}
}