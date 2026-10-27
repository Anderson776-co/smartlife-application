package co.com.smartlife.smartlifeapi.shared.service.usecase;

public interface UseCaseWithReturn <I,O>{
	
	O execute(I input);

}
