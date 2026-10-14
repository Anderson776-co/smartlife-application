package co.com.smartlife.smartlifeapi.visits.service.input.dto.response;

import java.time.LocalDateTime;

public class authorizeVisitDTOResponse {
	private String authorizationId;
	private String status;
	private String dwellingType;
	private String block;
	private String dwellingNumber;
	private String visitorDocumentType;
	private String visitorDocumentNumber;
	private String visitorFullName;
	private String vehiclePlate;
	private String visitorEmail;
	private String visitReason;
    private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String observation;
	private String qrCode;
}
