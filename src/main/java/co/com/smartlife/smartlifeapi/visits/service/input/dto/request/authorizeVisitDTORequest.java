package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

import java.time.LocalDateTime;

public class authorizeVisitDTORequest {
	private String dwellingType;
	private String block;
	private String dwellingNumber;
	private String visitorDocumentType;
	private String visitorDocumentNumber;
	private String visitorName;
	private String visitorLastname;
	private String vehiclePlate;
	private String visitorEmail;
	private String visitReason;
    private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String observation;
	private String supportDocumentId;
}
