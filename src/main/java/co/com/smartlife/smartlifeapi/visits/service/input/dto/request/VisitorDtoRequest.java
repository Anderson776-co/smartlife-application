package co.com.smartlife.smartlifeapi.visits.service.input.dto.request;

public record VisitorDtoRequest(String documentNumber, String firstName, String middleName, 
		String lastName, String secondLastName, String email, String vehiclePlate) {

}
