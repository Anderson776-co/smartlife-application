package co.com.smartlife.smartlifeapi.visits.service.output.port;

import java.time.Duration;

public interface TelemetryOutputPort {

	void incrementCounter(String name);

	void recordDuration(String name, Duration duration);
}