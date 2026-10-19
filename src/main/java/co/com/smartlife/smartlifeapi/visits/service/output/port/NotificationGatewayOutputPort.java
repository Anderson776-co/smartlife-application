package co.com.smartlife.smartlifeapi.visits.service.output.port;

import co.com.smartlife.smartlifeapi.visits.service.output.dto.EmailNotificationDTO;

public interface NotificationGatewayOutputPort {

	String sendEmail(EmailNotificationDTO email);
}