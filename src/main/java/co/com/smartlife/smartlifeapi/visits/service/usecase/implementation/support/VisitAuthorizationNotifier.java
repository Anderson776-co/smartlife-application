package co.com.smartlife.smartlifeapi.visits.service.usecase.implementation.support;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.com.smartlife.smartlifeapi.crosscutting.helper.LocalHelper;
import co.com.smartlife.smartlifeapi.crosscutting.helper.TextHelper;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationCatalogOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.NotificationGatewayOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.QrGeneratorOutputPort;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailAttachmentDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.NotificationCatalogDTO;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitAuthorizationEntity;
import co.com.smartlife.smartlifeapi.visits.service.output.port.repository.entity.VisitorEntity;

@Component
public class VisitAuthorizationNotifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisitAuthorizationNotifier.class);
	public static final String NOTIFICATION_CODE = "VISIT_AUTHORIZATION_CREATED";
	public static final String QR_CONTENT_ID = "visit-qr";
	private static final String QR_FILE_NAME = "autorizacion-visita.png";
	private static final String QR_CONTENT_TYPE = "image/png";
	private static final String DATE_PATTERN = "dd/MM/yyyy hh:mm a";
	// Temporal: cuando tengas VisitParametersOutputPort, reemplázalo por parameters.timeZone()
	private static final ZoneId TIME_ZONE = ZoneId.of("America/Bogota");

	private final NotificationCatalogOutputPort notificationCatalog;   // Strapi + @Cacheable (Redis)
	private final NotificationGatewayOutputPort notificationGateway;   // Resend
	private final QrGeneratorOutputPort qrGenerator;                   // ZXing

	public VisitAuthorizationNotifier(final NotificationCatalogOutputPort notificationCatalog,
			final NotificationGatewayOutputPort notificationGateway, final QrGeneratorOutputPort qrGenerator) {
		this.notificationCatalog = notificationCatalog;
		this.notificationGateway = notificationGateway;
		this.qrGenerator = qrGenerator;
	}

	/** Nunca lanza excepción: la autorización ya quedó guardada. */
	public boolean notifyVisitor(final VisitAuthorizationEntity authorization) {
		try {
			final String locale = LocalHelper.getCurrentLocal();
			final NotificationCatalogDTO template = notificationCatalog.findByCodeAndLocale(NOTIFICATION_CODE, locale);
			final Map<String, String> values = placeholders(authorization, Locale.forLanguageTag(locale));
			final String qrBase64 = qrGenerator.generateBase64(authorization.getQrCode());

			final String emailId = notificationGateway.sendEmail(new EmailNotificationDTO(
					authorization.getVisitor().getEmail(),
					render(template.subject(), values, false),
					render(template.content(), values, true),
					List.of(new EmailAttachmentDTO(QR_FILE_NAME, qrBase64, QR_CONTENT_TYPE, QR_CONTENT_ID))));
			LOGGER.info("Autorización {} notificada (Resend id={})", authorization.getId(), emailId);
			return true;
		} catch (final RuntimeException exception) {
			LOGGER.error("No se pudo notificar la autorización {}: {}", authorization.getId(),
					exception.getMessage(), exception);
			return false;
		}
	}

	private Map<String, String> placeholders(final VisitAuthorizationEntity authorization, final Locale locale) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale).withZone(TIME_ZONE);
		final VisitorEntity visitor = authorization.getVisitor();
		final Map<String, String> values = new HashMap<>();
		values.put("visitorName", join(visitor.getFirstName(), visitor.getMiddleName(),
				visitor.getLastName(), visitor.getSecondLastName()));
		values.put("authorizatorName", authorization.getAuthorizator().getFullName());
		values.put("location", authorization.getProperty().getLocation());
		values.put("reason", authorization.getVisitReason().getName());
		values.put("startDate", formatter.format(authorization.getAuthorizationStartDate()));
		values.put("endDate", formatter.format(authorization.getAuthorizationEndDate()));
		values.put("vehiclePlate", TextHelper.getDefault(visitor.getVehiclePlate()));
		values.put("qrUrl", authorization.getQrCode());
		return values;
	}

	private static String join(final String... parts) {
		return Arrays.stream(parts).filter(part -> !TextHelper.isEmptyWithTrim(part))
				.map(String::trim).collect(Collectors.joining(" "));
	}

	private static String render(final String template, final Map<String, String> values, final boolean escapeHtml) {
		String result = TextHelper.getDefault(template);
		for (final Map.Entry<String, String> entry : values.entrySet()) {
			final String value = TextHelper.getDefault(entry.getValue());
			result = result.replace("{{" + entry.getKey() + "}}", escapeHtml ? escape(value) : value);
		}
		return result;
	}

	private static String escape(final String value) {   // evita inyectar HTML con el nombre del visitante
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}