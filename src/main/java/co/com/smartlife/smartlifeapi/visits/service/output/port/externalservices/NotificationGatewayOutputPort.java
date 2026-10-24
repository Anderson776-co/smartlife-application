package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices;

import co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto.EmailNotificationDTO;

public interface NotificationGatewayOutputPort {

	String sendEmail(EmailNotificationDTO email);
}