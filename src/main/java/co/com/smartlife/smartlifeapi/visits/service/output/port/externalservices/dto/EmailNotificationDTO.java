package co.com.smartlife.smartlifeapi.visits.service.output.port.externalservices.dto;

import java.util.List;

import co.com.smartlife.smartlifeapi.crosscutting.helper.ObjectHelper;

public record EmailNotificationDTO(String to, String subject, String content, List<EmailAttachmentDTO> attachments) {
	
	public EmailNotificationDTO{
		attachments = ObjectHelper.isNull(attachments) ? List.of() : List.copyOf(attachments);
	}
	
	public EmailNotificationDTO(final String to, final String subject, final String content) {
		this(to, subject, content, List.of());
	}
	
}