package co.com.smartlife.smartlifeapi.visits.infrastructure.output.adapter.externalservices.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

	/** Reloj del sistema en UTC; los casos de uso lo reciben para poder probar las reglas de fechas. */
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

}
