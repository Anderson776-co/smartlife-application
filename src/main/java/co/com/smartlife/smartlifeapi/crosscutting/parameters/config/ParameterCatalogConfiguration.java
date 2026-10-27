package co.com.smartlife.smartlifeapi.crosscutting.parameters.config;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import co.com.smartlife.smartlifeapi.crosscutting.parameters.ParameterCatalog;
import co.com.smartlife.smartlifeapi.crosscutting.parameters.VisitParameters;
import jakarta.annotation.PostConstruct;

/**
 * Lee los parametros (del servidor de parametros o de los valores de respaldo)
 * y los deja disponibles en ParameterCatalog.
 */
@Configuration
public class ParameterCatalogConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterCatalogConfiguration.class);

	@Value("${smartlife.visits.max-authorization-duration:24h}")
	private Duration maxAuthorizationDuration;

	@Value("${smartlife.visits.max-anticipation:30d}")
	private Duration maxAnticipation;

	@Value("${smartlife.visits.start-tolerance:10m}")
	private Duration startTolerance;

	@Value("${smartlife.visits.document.max-size:5MB}")
	private DataSize documentMaxSize;

	@Value("${smartlife.visits.document.allowed-types:application/pdf,image/jpeg,image/png}")
	private List<String> documentAllowedTypes;

	@Value("${smartlife.visits.qr-base-url:http://localhost:5173/visitas/autorizaciones/}")
	private String qrBaseUrl;

	@Value("${smartlife.visits.time-zone:UTC}")
	private String timeZone;

	@PostConstruct
	public void loadParameters() {
		final VisitParameters visitParameters = new VisitParameters(maxAuthorizationDuration, maxAnticipation,
				startTolerance, documentMaxSize, documentAllowedTypes, qrBaseUrl, ZoneId.of(timeZone));
		ParameterCatalog.load(visitParameters);
		LOGGER.info(
				"Parametros de visitas cargados: duracion maxima={}, anticipacion maxima={}, tolerancia de inicio={}, "
						+ "tamano maximo del documento={}, tipos permitidos={}, zona horaria={}",
				visitParameters.maxAuthorizationDuration(), visitParameters.maxAnticipation(),
				visitParameters.startTolerance(), visitParameters.documentMaxSize(),
				visitParameters.documentAllowedTypes(), visitParameters.timeZone());
	}
}